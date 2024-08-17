package com.moonraft.search.domain.service;

import com.moonraft.search.domain.command.CrawlCommand;
import com.moonraft.search.domain.command.VectorizeCommand;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.CrawlOrder;
import com.moonraft.search.domain.model.CrawlOrderLedger;
import com.moonraft.search.domain.model.OrderStatus;
import com.moonraft.search.domain.repository.SearchRepository;
import com.moonraft.search.domain.service.api.ESService;
import com.moonraft.search.domain.service.api.VectorService;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class WebCrawlerServiceMulti{

    private VectorService vectorService;

    private SearchRepository searchRepository;

    private ESService esService;

    private CrawlOrderLedger crawlOrderLedger;

    private CrawlCommand crawlCommand;

    private Integer requestID;

    public WebCrawlerServiceMulti(VectorService vectorService, ESService esService, SearchRepository searchRepository, CrawlOrderLedger crawlOrderLedger) {
        this.vectorService = vectorService;
        this.searchRepository = searchRepository;
        this.esService = esService;
        this.crawlOrderLedger = crawlOrderLedger;

    }


    @Async
    public void crawl(CrawlCommand crawlCommand, Integer requestID) throws Exception {

        log.info(String.format("Entering crawl service - URL :%s", crawlCommand.getSeedUrl()));

        crawlOrderLedger.getLedger().put(requestID, new CrawlOrder());
        crawlOrderLedger.incrementOrderCount();
        crawlOrderLedger.getPendingRequests().add(requestID);
        log.info("Number of crawls pending - {}", crawlOrderLedger.getOrderCount());
        String rootFolder = "data/crawl/root" + "_" + requestID.toString();
        final int numberOfCrawlers = AppConstants.INSTANCES;
        final String domain = crawlCommand.getDomain();
        String seedURL;
        if (crawlCommand.getSeedUrl().charAt(crawlCommand.getSeedUrl().length() -1) == '/') {
            seedURL = crawlCommand.getSeedUrl().substring(0,crawlCommand.getSeedUrl().length() - 1);
        }
        else{
            seedURL = crawlCommand.getSeedUrl();
        }
        crawlOrderLedger.getLedger().get(requestID).setSeedURL(seedURL);
        log.info("the Request ID processing : {}", requestID.intValue());

        CrawlConfig config = new CrawlConfig();
        config.setCrawlStorageFolder(rootFolder);
        config.setPolitenessDelay(1000);
//        config.setMaxPagesToFetch(30);


        PageFetcher pageFetcher = new PageFetcher(config);
        RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
        RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);

        CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
        controller.addSeed(seedURL);
        crawlOrderLedger.getCrawlControllerPool().put(requestID, controller);
        crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.CRAWL_STARTED);
        crawlOrderLedger.getCrawlControllerPool().get(requestID).start(new CustomCrawlerFactory(crawlOrderLedger.getLedger().get(requestID)), numberOfCrawlers);
        log.info("The crawled Pages for request {} is {}", requestID.toString(), crawlOrderLedger.getLedger().get(requestID).getWebPages().getUrl().size());

//        List<Object> crawlersLocalData = crawlOrderLedger.getCrawlControllerPool().get(requestID).getCrawlersLocalData();
//        long totalLinks = 0;
//        long totalTextSize = 0;
//        int totalProcessedPages = 0;
//        for (Object localData : crawlersLocalData) {
//
//            CrawlOrder crawlinfo = (CrawlOrder) localData;
//            totalLinks += crawlinfo.getLinkCount();
//            totalTextSize += crawlinfo.getTextSize();
//            totalProcessedPages += crawlinfo.getPageCount();
//        }
//
//        log.info("Aggregated Statistics:\tProcessed Pages: {}\tTotal Links found: {}\tTotal Text Size: {}\tEnd of execution",
//                totalProcessedPages, totalLinks, totalTextSize);
//        log.info("Total pages fetched for indexing : {}", crawlOrderLedger.getLedger().get(requestID).getPageCount());

        if (crawlOrderLedger.getLedger().get(requestID).getOrderStatus() == OrderStatus.CRAWL_TERMINATION_REQUESTED){
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.CRAWL_TERMINATED);
            log.info("Crawl status : {}", crawlOrderLedger.getLedger().get(requestID).getOrderStatus());
            return;
        }
        crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.CRAWL_COMPLETED);
        int wordOverlap = 300;
        int chunkLength = 1000;
        int totalPages = crawlOrderLedger.getLedger().get(requestID).getPageCount();
        if (crawlOrderLedger.getLedger().get(requestID).getPageCount() != 0) {

            VectorizeCommand vectorizeCommand =  VectorizeCommand.builder().pages(crawlOrderLedger.getLedger().get(requestID).getWebPages().getPage().toArray(new String[totalPages]))
                    .Urls(crawlOrderLedger.getLedger().get(requestID).getWebPages().getUrl().toArray(new String[totalPages])).titles(crawlOrderLedger.getLedger().get(requestID).getWebPages().getTitle().toArray(new String[totalPages]))
                    .wordOverlap(wordOverlap).chunkLength(chunkLength).build();

            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.VECTORIZATION_STARTED);
            float[][] vectorChunks = vectorService.vectorize(vectorizeCommand, requestID);

            if (crawlOrderLedger.getLedger().get(requestID).getOrderStatus() == OrderStatus.CRAWL_TERMINATION_REQUESTED){
                crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.CRAWL_TERMINATED);
                return;
            }
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.INDEXING_STARTED);
            searchRepository.createIndex(AppConstants.INDEX + "_" + crawlCommand.getDomain());

            esService.processPage(AppConstants.INDEX + "_" + crawlCommand.getDomain() , vectorChunks, vectorService.getChunks(), vectorService.getChunks().size());

            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.FINISHED_INDEXING);

            if (crawlOrderLedger.getLedger().get(requestID).getOrderStatus() == OrderStatus.CRAWL_TERMINATION_REQUESTED){
                crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.CRAWL_NOT_TERMINATED);
                return;
            }
            log.info("Finished indexing");
        } else {
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.ERROR_ENCOUNTERED);
            log.info("No Pages fetched");
        }

    }
}
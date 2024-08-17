package com.moonraft.search.application.controller;

import com.moonraft.search.application.controller.request.CrawlRequest;
import com.moonraft.search.application.mapper.InputOutputMapper;
import com.moonraft.search.domain.command.CrawlCommand;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.CrawlOrderLedger;
import com.moonraft.search.domain.model.CrawlResponse;
import com.moonraft.search.domain.model.OrderStatus;
import com.moonraft.search.domain.repository.SearchRepository;
import com.moonraft.search.domain.service.WebCrawlerServiceMulti;
import com.moonraft.search.domain.service.api.ESService;
import com.moonraft.search.domain.service.api.VectorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class CrawlController {

    private CrawlOrderLedger crawlOrderLedger;

    private SearchRepository searchRepository;

    private ESService esService;
    private VectorService vectorService;

    private WebCrawlerServiceMulti webCrawlerServiceMulti;


    @Autowired
    public CrawlController( CrawlOrderLedger crawlOrderLedger, VectorService vectorService, ESService esService, SearchRepository searchRepository, WebCrawlerServiceMulti webCrawlerServiceMulti) {
        this.crawlOrderLedger = crawlOrderLedger;
        this.vectorService = vectorService;
        this.esService = esService;
        this.searchRepository=searchRepository;
        this.webCrawlerServiceMulti = webCrawlerServiceMulti;
    }

    @PostMapping("crawl")
    public CrawlResponse crawl(@RequestBody CrawlRequest crawlRequest) throws Exception {
        log.info(String.format("Entering crawl controller - Search Query:%s", crawlRequest.getSeedUrl()));
        CrawlCommand crawlCommand = InputOutputMapper.buildCrawlCommandFromCrawlRequest.apply(crawlRequest, AppConstants.USER);
        Integer requestID = new Random().nextInt(90000) + 10000;

        //WebCrawlerServiceMulti webCrawlerServiceMulti = new WebCrawlerServiceMulti(vectorService, esService, searchRepository, crawlOrderLedger);
        webCrawlerServiceMulti.crawl(crawlCommand, requestID);

        return CrawlResponse.builder().requestID(requestID.intValue()).domain(crawlCommand.getDomain()).build();
    }

    @GetMapping("crawl/status")
    public OrderStatus getStatus(@RequestParam("id") int id) throws Exception{
        log.info(String.format("Entering crawl controller - Crawl Id y:%s", id));
        return crawlOrderLedger.getLedger().get(Integer.valueOf(id)).getOrderStatus();
    }

    @GetMapping("crawl/all_ids")
    public ResponseEntity<List<Integer>> getStatusList(){
       return new ResponseEntity<>(crawlOrderLedger.getPendingRequests(), HttpStatus.ACCEPTED);
    }

    @GetMapping("crawl/stop_crawl")
    public OrderStatus stopCrawl(@RequestParam("id") int id) {

        Integer requestId = id;
        if (crawlOrderLedger.getLedger().get(requestId).getOrderStatus() == OrderStatus.CRAWL_STARTED) {
            crawlOrderLedger.getCrawlControllerPool().get(requestId).shutdown();
            crawlOrderLedger.decrementOrderCount();
            crawlOrderLedger.getCrawlControllerPool().remove(requestId);
            crawlOrderLedger.getLedger().get(requestId).setOrderStatus(OrderStatus.CRAWL_TERMINATION_REQUESTED);
        } else if (crawlOrderLedger.getLedger().get(requestId).getOrderStatus() == OrderStatus.VECTORIZATION_STARTED ||
                crawlOrderLedger.getLedger().get(requestId).getOrderStatus() == OrderStatus.INDEXING_STARTED){
            crawlOrderLedger.decrementOrderCount();
            crawlOrderLedger.getCrawlControllerPool().remove(requestId);
            crawlOrderLedger.getLedger().get(requestId).setOrderStatus(OrderStatus.CRAWL_TERMINATION_REQUESTED);
        } else{
            return crawlOrderLedger.getLedger().get(requestId).getOrderStatus();
        }
        return crawlOrderLedger.getLedger().get(requestId).getOrderStatus();

        // To get the actual status of termination of request, multiple stopCrawl API hits are required to verify if the crawl status changed from
        // "CRAWL_TERMINATION_REQUESTED" to "CRAWL_TERMINATED"
        // This needs to be explicitly mentioned in the front-end

        // MORE LOGIC REQUIRED HERE to clean the crawl data folder



    }
}
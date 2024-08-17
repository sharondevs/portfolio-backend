package com.moonraft.search.domain.service;

import com.moonraft.search.domain.model.CrawlOrder;
import com.moonraft.search.domain.util.VectorUtils;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.regex.Pattern;

@Slf4j
@Component
public class WebPageCrawler extends WebCrawler {
    private static final Pattern FILTERS = Pattern.compile(
            ".*(\\.(css|js|bmp|gif|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v|pdf" +
                    "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

    private CrawlOrder crawlOrder;


    public WebPageCrawler(CrawlOrder crawlOrder) {
        this.crawlOrder = crawlOrder;
    }

    @Override
    public boolean shouldVisit(Page referringPage, WebURL url) {

//        String
        String href = url.getURL().toLowerCase();
        return !FILTERS.matcher(href).matches() && href.startsWith(crawlOrder.getSeedURL());
    }

    @Override
    public void visit(Page page) {
        logger.info("Visited: {}", page.getWebURL().getURL());
        crawlOrder.incrementPageCount();

        if (page.getParseData() instanceof HtmlParseData) {
            HtmlParseData parseData = (HtmlParseData) page.getParseData();
            logger.info("Adding Page, URL: {}", page.getWebURL().getURL());
            crawlOrder.getWebPages().getPage().add(VectorUtils.toText(parseData));
            crawlOrder.getWebPages().getUrl().add(page.getWebURL().getURL());
            crawlOrder.getWebPages().getTitle().add(parseData.getTitle());
            logger.info("Added Page, Url: {}", page.getWebURL().getURL());
            Set<WebURL> links = parseData.getOutgoingUrls();
            crawlOrder.incrementLinkCount(links.size());
            crawlOrder.incrementTextSize(parseData.getText().getBytes(StandardCharsets.UTF_8).length);
        }
        if (crawlOrder.getPageCount() % 50 == 0) printStatus();

    }

    @Override
    public Object getMyLocalData() {
        return crawlOrder;
    }

    @Override
    public void onBeforeExit() {
        printStatus();
    }

    public void printStatus() {
        logger.info("Crawler {} > Processed Pages: {}, Total Links Found: {}, Total Text Size: {}",
                getMyId(), crawlOrder.getPageCount(), crawlOrder.getLinkCount(), crawlOrder.getTextSize());
    }
}

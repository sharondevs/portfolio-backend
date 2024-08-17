package com.moonraft.search.domain.service;

import com.moonraft.search.domain.model.CrawlOrder;
import edu.uci.ics.crawler4j.crawler.CrawlController;

public class CustomCrawlerFactory implements CrawlController.WebCrawlerFactory<WebPageCrawler> {


    private CrawlOrder crawlOrder;

    public CustomCrawlerFactory(CrawlOrder crawlOrder){
        this.crawlOrder = crawlOrder;
    }


    @Override
    public WebPageCrawler newInstance() throws Exception {
        return new WebPageCrawler(crawlOrder);
    }
}

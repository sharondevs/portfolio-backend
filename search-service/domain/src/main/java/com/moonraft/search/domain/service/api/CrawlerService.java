package com.moonraft.search.domain.service.api;

import com.moonraft.search.domain.command.CrawlCommand;


public interface CrawlerService {
    void crawl(CrawlCommand crawlCommand, Integer requestID) throws Exception;

}

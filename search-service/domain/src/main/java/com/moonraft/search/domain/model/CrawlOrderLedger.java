package com.moonraft.search.domain.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CrawlOrderLedger {

    ConcurrentHashMap<Integer,CrawlOrder> ledger = new ConcurrentHashMap<>();

    ConcurrentHashMap<Integer, CrawlController> crawlControllerPool = new ConcurrentHashMap<>();


    ArrayList<Integer> pendingRequests = new ArrayList<>();

    private int orderCount = 0;

    public void incrementOrderCount(){
        orderCount++;
    }

    public void decrementOrderCount(){
        orderCount--;
    }

}

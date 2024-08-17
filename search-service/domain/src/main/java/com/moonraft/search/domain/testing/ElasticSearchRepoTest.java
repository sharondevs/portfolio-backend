package com.moonraft.search.domain.testing;

import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.repository.ElasticSearchRepository;
import com.moonraft.search.domain.repository.SearchRepository;
import com.moonraft.search.domain.service.SearchRequestService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//@SpringBootApplication(scanBasePackages = {"com.moonraft.search"})
//public class ElasticSearchRepoTest {
//
//    public static void main(String[] args) {
//
//        SpringApplication.run(ElasticSearchRepoTest.class, args);
//        SearchRequestService searchRequestService = new SearchRequestService(new ElasticSearchRepository(new AppConfig()), new AppConfig());
//        System.out.println(searchRequestService.search(SearchCommand.builder().searchQuery("who is the ceo?").domain("vanguard_approxknn_instructor").build()).get(0));
//
//
//    }
//}
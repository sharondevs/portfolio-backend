package com.moonraft.search.application.controller;

import com.moonraft.search.application.controller.request.SearchRequest;
import com.moonraft.search.application.mapper.InputOutputMapper;
import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.SearchResult;
import com.moonraft.search.domain.service.api.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/")
public class SearchController {

    private final SearchService searchService;

    @Autowired
    public SearchController(final SearchService searchService) {
        this.searchService = searchService;
    }

    @PostMapping("search")
    public SearchResult search(@RequestBody SearchRequest searchRequest) {
        log.info(String.format("Entering search controller - Search Query:%s", searchRequest.getSearchQuery()));
        SearchCommand searchCommand = InputOutputMapper.buildSearchCommandFromSearchRequest.apply(searchRequest, AppConstants.USER);
        return searchService.search(searchCommand);
    }
}
package com.moonraft.search.domain.service.api;

import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.model.SearchResult;

import java.util.List;

public interface SearchService {
    SearchResult search(SearchCommand searchCommand);

}

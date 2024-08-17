package com.moonraft.search.domain.service;

import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.ESSearchRequest;
import com.moonraft.search.domain.model.FastAPIRequest;
import com.moonraft.search.domain.model.SearchResult;
import com.moonraft.search.domain.repository.SearchRepository;
import com.moonraft.search.domain.repository.UserRepository;
import com.moonraft.search.domain.service.api.SearchService;
import com.moonraft.search.domain.util.ContentLimiter;
import com.moonraft.search.domain.util.SearchUtils;
import com.moonraft.search.domain.util.VectorUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SearchRequestService implements SearchService {


    private SearchRepository searchRepository;

    private FastAPIRequest fastAPIRequest;

    private AppConfig appConfig;
    private UserRepository userRepository;

    @Autowired
    public SearchRequestService(SearchRepository searchRepository, AppConfig appConfig){
        this.appConfig = appConfig;
        this.searchRepository = searchRepository;
        this.fastAPIRequest = FastAPIRequest.builder().port(Integer.parseInt(appConfig.getFastapiPort()))
                .path(AppConstants.FASTAPI_SEARCH_PATH).domain(appConfig.getFastapiHost()).build();
    }

    @Override
    public SearchResult search(SearchCommand searchCommand) {

        log.info(String.format("Entering search service - Search Query:%s", searchCommand.getSearchQuery()));

        return ContentLimiter.contentLimit(SearchUtils.responseParser(searchRepository.sendSearchRequest(ESSearchRequest.builder()
                .sourcesToFetch(AppConstants.SEARCH_RESULT_SOURCES)
                .k(AppConstants.K).index("webdomain_" + searchCommand.getDomain())
                .field("vector").numOfCandidates(AppConstants.NUM_OF_CANDIDATES)
                .queryVector(VectorUtils.vectorizeQuery(searchCommand.getSearchQuery(),fastAPIRequest)).build())), searchCommand, appConfig);


    }
}

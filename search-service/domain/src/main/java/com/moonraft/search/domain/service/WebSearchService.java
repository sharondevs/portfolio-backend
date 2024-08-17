package com.moonraft.search.domain.service;

import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.model.*;
import com.moonraft.search.domain.repository.SearchRepository;
import com.moonraft.search.domain.service.api.ESService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Slf4j
@Service
public class WebSearchService implements ESService {
    private SearchRepository searchRepository;

    private AppConfig appConfig;
    @Autowired
    public WebSearchService(SearchRepository searchRepository, AppConfig appConfig) {
        this.searchRepository = searchRepository;
        this.appConfig = appConfig;
    }


    @Override
    public void processPage(String index, float[][] vectorData, List<String[]> plainData, int numberOfDataPoints) {

        for (int i = 0; i < numberOfDataPoints; i++) {

            searchRepository.insertDocument( new DocumentChunk(plainData.get(i)[0], vectorData[i], plainData.get(i)[1], plainData.get(i)[2]), "Chunk " + i, index);
            // Passing the extracted data to the elasticsearch server instance for indexing

        }
    }


}

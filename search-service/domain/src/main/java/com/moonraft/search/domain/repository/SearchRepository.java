package com.moonraft.search.domain.repository;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.moonraft.search.domain.model.DocumentChunk;
import com.moonraft.search.domain.model.ESSearchRequest;
import com.moonraft.search.domain.model.SearchItem;

import java.util.List;


public interface SearchRepository  {

    SearchResponse<JsonData> sendSearchRequest(ESSearchRequest esSearchRequest);

    void insertDocument(DocumentChunk document, String docId, String index) ;

    void createIndex(String index);


}

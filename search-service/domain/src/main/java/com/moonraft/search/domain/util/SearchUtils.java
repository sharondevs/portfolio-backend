package com.moonraft.search.domain.util;

import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;
import com.moonraft.search.domain.model.SearchItem;
import com.moonraft.search.domain.model.SearchResult;
import jakarta.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class SearchUtils {

    public static SearchResult responseParser(SearchResponse<JsonData> searchResponse) {

        List<SearchItem> hits = new ArrayList<>();
        List<String> unique = new ArrayList<>();
        int num_hits = 0;
        for (Hit<JsonData> individualSource: searchResponse.hits().hits()) {


            JsonObject sourceJson = individualSource.source().toJson().asJsonObject();
            SearchItem resultBundle = SearchItem.builder().content(sourceJson.getJsonString("content").toString()).title(sourceJson.getJsonString("title").toString().replaceAll("\"",""))
                            .url(sourceJson.getJsonString("url").toString().replaceAll("\"","")).build();

            if (unique.contains(resultBundle.getTitle()) == false) {
                hits.add(resultBundle);
                num_hits += 1;
                unique.add(resultBundle.getTitle());
            }

        }
        return SearchResult.builder().hit(num_hits).hits(hits).build();
    }

}

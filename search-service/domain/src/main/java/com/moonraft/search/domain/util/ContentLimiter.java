package com.moonraft.search.domain.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moonraft.search.domain.command.SearchCommand;
import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.SearchItem;
import com.moonraft.search.domain.model.SearchResult;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Slf4j
public class ContentLimiter {

    public static SearchResult contentLimit(SearchResult searchResult, SearchCommand searchCommand, AppConfig appConfig){

        List<String> chunks =  new ArrayList<>();

        for(SearchItem searchItem : searchResult.getHits()){
            chunks.add(searchItem.getContent());
        }
        int docLen = chunks.size();
        JsonArray jsonArray = new JsonArray();
        for (String chunk : chunks) {
            jsonArray.add(chunk);
        }

        JsonObject json = new JsonObject();
        json.addProperty("query", searchCommand.getSearchQuery());
        json.add("chunks", jsonArray);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(appConfig.getVectorizeApi() + AppConstants.FASTAPI_CONTENT_LIMIT);
        try {
            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                ObjectMapper objectMapper = new ObjectMapper();
                String responseString = EntityUtils.toString(responseEntity);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseString, JsonObject.class);

                JsonArray chunkJson = jsonObject.getAsJsonArray("limited_chunks");
                String[] limitedChunks = new String[docLen];
                int i = 0;
                for (JsonElement chunk : chunkJson) {
                    limitedChunks[i] = gson.fromJson(chunk, String.class);
                    i++;
                }
                response.close();
                httpClient.close();

                int content_index = 0;
                for(SearchItem searchItem : searchResult.getHits()) {
                    searchItem.setContent(limitedChunks[content_index]);
                    content_index++;
                }
                return searchResult;

            }

            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }
}

package com.moonraft.search.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moonraft.search.domain.command.VectorizeCommand;
import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.CrawlOrderLedger;
import com.moonraft.search.domain.model.OrderStatus;
import com.moonraft.search.domain.service.api.VectorService;
import com.moonraft.search.domain.util.VectorUtils;
import lombok.Getter;
import lombok.Setter;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class VectorProcessorService implements VectorService {

    @Getter @Setter private List<String[]> chunks;
    private AppConfig appConfig;

    public CrawlOrderLedger crawlOrderLedger;

    @Autowired
    public VectorProcessorService(AppConfig appConfig, CrawlOrderLedger crawlOrderLedger) {
        this.appConfig = appConfig;
        this.crawlOrderLedger = crawlOrderLedger;
    }

    public float[][] vectorize(VectorizeCommand vectorizeCommand, Integer requestID) {

        float[][] chunkVectors;
        this.chunks = VectorUtils.toChunks(vectorizeCommand.getPages(), vectorizeCommand.getWordOverlap(), vectorizeCommand.getChunkLength(), vectorizeCommand.getUrls(), vectorizeCommand.getTitles());
        int totalChunks = this.chunks.size();
        String[][] chunkDataArray = this.chunks.toArray(new String[totalChunks][]);
        String[] chunkToVectorize = new String[totalChunks];

        for (int i = 0; i < totalChunks; i++) {
            chunkToVectorize[i] = chunkDataArray[i][0];
        }
        chunkVectors = generateVector(chunkToVectorize, requestID);

        return chunkVectors;
    }

    private float[][] generateVector(String[] docInChunks, Integer requestID) {

        List<String> chunks = Arrays.asList(docInChunks);
        int docLen = chunks.size();

        JsonArray jsonArray = new JsonArray();
        for (String chunk : chunks) {
            jsonArray.add(chunk);
        }

        JsonObject json = new JsonObject();
        json.add("chunks", jsonArray);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost(appConfig.getVectorizeApi() + AppConstants.FASTAPI_CRAWL_PATH);
        try {
            StringEntity entity = new StringEntity(json.toString(), ContentType.APPLICATION_JSON);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();

            if (responseEntity != null) {
                String responseString = EntityUtils.toString(responseEntity);
                Gson gson = new Gson();
                JsonObject jsonObject = gson.fromJson(responseString, JsonObject.class);
                JsonArray arrayVectors = jsonObject.getAsJsonArray("vectors");
                float[][] vectorizedArray = new float[docLen][];
                int i = 0;
                for (JsonElement vector : arrayVectors) {
                    vectorizedArray[i] = gson.fromJson(vector, float[].class);
                    i++;
                }
                response.close();
                httpClient.close();
                return vectorizedArray;
            }

            response.close();
        } catch (UnsupportedEncodingException e) {
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.ERROR_ENCOUNTERED);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.ERROR_ENCOUNTERED);
            e.printStackTrace();
        } catch (IOException e) {
            crawlOrderLedger.getLedger().get(requestID).setOrderStatus(OrderStatus.ERROR_ENCOUNTERED);
            e.printStackTrace();
        }

        return null;
    }

}

package com.moonraft.search.domain.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.FastAPIRequest;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class VectorUtils {

    private VectorUtils() {
    }

    public static String toText(HtmlParseData parsedPage) {
        Document parsedContent = Jsoup.parse(parsedPage.getHtml());
        return parsedContent.text();
    }

    public static String cleanText(String pageText) {
        return pageText.replaceAll("[^a-zA-Z_0-9\\s]", " ");
    }

    public static List<String[]> toChunks(String[] pages, int wordOverlap, int chunkLength, String[] Urls, String[] titles) {

        List<String[]> chunkData = new ArrayList<String[]>();
        int numPages = pages.length;

        String chunk;
        int i, j;
        for (i = 0; i < numPages; i++) {
            String[] pageTokens = pages[i].split(" ");
            int tokenNum = pageTokens.length;
            int j_limit = tokenNum - chunkLength;
            for (j = 0; j < j_limit; j += chunkLength - wordOverlap) {
                String[] chunkArr;
                chunkArr = Arrays.copyOfRange(pageTokens, j, j + chunkLength);
                chunk = String.join(" ", chunkArr);
                chunkData.add(new String[]{chunk, Urls[i], titles[i]});
            }
            String[] chunkArr = Arrays.copyOfRange(pageTokens, j, tokenNum);
            chunk = String.join(" ", chunkArr);
            chunkData.add(new String[]{VectorUtils.cleanText(chunk), Urls[i], titles[i]});
        }
        return chunkData;
    }

    public static Float[] vectorizeQuery(String query, FastAPIRequest fastAPIRequest) {

        JsonArray queryString = new JsonArray();
        queryString.add(query);

        JsonObject requestPayload = new JsonObject();

        requestPayload.add("chunks", queryString);

        CloseableHttpClient httpClient = HttpClients.createDefault();

        HttpPost httpPost = new HttpPost("http://" + fastAPIRequest.getDomain() + ":" + fastAPIRequest.getPort()
                + "/" + fastAPIRequest.getPath());

        try {
            StringEntity entity = new StringEntity(requestPayload.toString(), ContentType.APPLICATION_JSON);

            httpPost.setEntity(entity);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            CloseableHttpResponse response = httpClient.execute(httpPost);
            HttpEntity responseEntity = response.getEntity();
            Gson gson = new Gson();
            if (responseEntity != null) {

                String responseString = EntityUtils.toString(responseEntity);

                JsonObject jsonObject = gson.fromJson(responseString, JsonObject.class);

                JsonArray arrayVectors = jsonObject.getAsJsonArray("vectors");

                Float[] vectorizedQuery = new Float[AppConstants.VECTORDIM];

                int i = 0;
                for (JsonElement vector : arrayVectors) {
                    vectorizedQuery = gson.fromJson(vector, Float[].class);
                }

                response.close();
                httpClient.close();
                return vectorizedQuery;
            }

            response.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}

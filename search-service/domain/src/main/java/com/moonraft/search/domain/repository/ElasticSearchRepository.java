package com.moonraft.search.domain.repository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch.core.IndexResponse;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.SourceConfig;
import co.elastic.clients.elasticsearch.core.search.SourceFilter;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.json.JsonData;
import com.moonraft.search.domain.config.AppConfig;
import com.moonraft.search.domain.config.AppConstants;
import com.moonraft.search.domain.model.Credential;
import com.moonraft.search.domain.model.DocumentChunk;
import com.moonraft.search.domain.model.ESSearchRequest;
import com.moonraft.search.domain.model.EndPoint;
import com.moonraft.search.domain.util.ESUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;

@Slf4j
@Repository
public class ElasticSearchRepository implements SearchRepository {

    private AppConfig appConfig;

    private ElasticsearchClient elasticsearchClient;



    @Autowired
    public ElasticSearchRepository(AppConfig appConfig) {
        this.appConfig = appConfig;

        Credential credentials = Credential.builder().userName(appConfig.getEsLogin()).password(appConfig.getEsPassword()).build();

        EndPoint serverEndpoint = EndPoint.builder().host(appConfig.getElasticSearchHost()).port(Integer.parseInt(appConfig.getElasticSearchPort())).protocol("https").build();
        try {
            File file = ResourceUtils.getFile("classpath:"+AppConstants.CERT_PATH);
            this.elasticsearchClient = ESUtils.buildESClient( file, credentials, serverEndpoint);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }



  //Searching
    @Override
    public SearchResponse<JsonData> sendSearchRequest(ESSearchRequest esSearchRequest) {

        try {

            KnnQuery.Builder knnQueryBuilder = new KnnQuery.Builder().field(esSearchRequest.getField())
                    .queryVector(Arrays.asList(esSearchRequest.getQueryVector()))
                    .k(esSearchRequest.getK()).numCandidates(esSearchRequest.getNumOfCandidates());

            SearchRequest.Builder searchRequestBuilder = new SearchRequest.Builder();

            SourceConfig.Builder SourceConfigBuilder = new SourceConfig.Builder();

            SourceConfig sourceConfig =  SourceConfigBuilder.filter(new SourceFilter.Builder().includes(new ArrayList<String>(Arrays.asList("content", "url", "title"))).build()).build();


            SearchRequest request =  searchRequestBuilder.index(esSearchRequest.getIndex()).source(sourceConfig).knn(knnQueryBuilder.build()).build();


            SearchResponse<JsonData> searchResponse = elasticsearchClient.search(request, JsonData.class);



            return searchResponse;

        } catch (IOException e) {

            e.printStackTrace();
        }

        return null;
    }
 

    //Crawl
    @Override
    public void insertDocument(DocumentChunk document, String docId, String index) {
        try {


            // adding / updating existing docs against the index
            IndexResponse response = elasticsearchClient.index(i -> i.index(index).id(docId).document(document));

            // closing the http client

            log.info("Indexed this chunk : {} from webpage {} and the reponse was {}", docId, document.getUrl(), response.version());

        } catch (IOException e) {
            // TODO Auto-generated catch block
            log.info("Invalid or expired certificate, PLEASE CHECK CERTIFICATE PATH");
        }

    }

    @Override
    public void createIndex(String index) {

        log.info("Starting to create the index....");


        //Creating the settings builder object
        CreateIndexRequest.Builder requestBuilder = new CreateIndexRequest.Builder();

        requestBuilder.index(index);
        //Creating a JSON-formatted string for setting the index request for Approx KNN
        String settingsJson = "{\n"
                + " \"mappings\": {\n"
                + "		\"properties\": {\n"
                + "			\"vector\": {\n"
                + "				\"type\": \"dense_vector\",\n"
                + "				\"dims\": 768,\n"
                + "				\"index\": true,\n"
                + "				\"similarity\": \"dot_product\"\n"
                + "			}\n"
                + "		}\n"
                + "}\n"
                + "}";


        try (Reader readerObj = new StringReader(settingsJson)) {

            //this feeds the JSON string as a Reader object to CreateIndexRequest.Builder
            requestBuilder.withJson(readerObj);

            elasticsearchClient.indices().create(requestBuilder.build());

            log.info("Index created successfully");


        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }
}

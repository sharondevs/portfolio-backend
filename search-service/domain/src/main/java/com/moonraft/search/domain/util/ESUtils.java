package com.moonraft.search.domain.util;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.TransportUtils;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.moonraft.search.domain.model.Credential;
import com.moonraft.search.domain.model.EndPoint;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;

public class ESUtils {
    private ESUtils() {
    }

    public static ElasticsearchClient buildESClient(File certFile, Credential credentials, EndPoint serverEndpoint) throws IOException {

        SSLContext sslContext = TransportUtils.sslContextFromHttpCaCrt(certFile);
        BasicCredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(credentials.getUserName(), credentials.getPassword()));

        RestClient restClient = RestClient.builder(new HttpHost(serverEndpoint.getHost(), serverEndpoint.getPort(), serverEndpoint.getProtocol()))
                .setHttpClientConfigCallback(
                        httpClient -> httpClient.setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).setDefaultCredentialsProvider(credProvider))
                .build();
        ElasticsearchTransport transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        ElasticsearchClient EsClient = new ElasticsearchClient(transport);
        return EsClient;
    }

}

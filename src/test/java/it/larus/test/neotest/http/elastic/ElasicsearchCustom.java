package it.larus.test.neotest.http.elastic;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class ElasicsearchCustom {

    private static RestHighLevelClient clientES;

    private void setUpConnection() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials( "", "" )
        );

        log.info( "establishing connection to ES on: " + System.getenv( "ES_IP" ) );

        RestClientBuilder restClient = RestClient.builder( new HttpHost( "127.0.0.1", 9200, "http" ) )
                .setHttpClientConfigCallback( ( HttpAsyncClientBuilder httpClientBuilder ) ->
                        httpClientBuilder.setDefaultCredentialsProvider( credentialsProvider ) );

        clientES = new RestHighLevelClient( restClient );
        try{
            log.info( "ClientES established: " + clientES.ping( RequestOptions.DEFAULT ) );
        }
        catch( IOException e ){
            log.error( "ClientES error {}", e.getMessage());
            System.exit( -1 );
        }
    }

    @BeforeEach
    public void setUp() {
        setUpConnection();
    }

    @Test
    public void testGetByDenominazione() throws IOException {
        SearchRequest searchRequest = new SearchRequest("persone_fisiche");
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery("denominazione", "damiano damiano").maxExpansions(10));
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = clientES.search(searchRequest, RequestOptions.DEFAULT);
        response.getHits().forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("denominazione");

            log.info("res {} {}", name, hit.getScore());
        });


        assertTrue(response.getHits().getHits().length > 0);
    }

}

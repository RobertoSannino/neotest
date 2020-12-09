package it.larus.test.neotest.elastic.repository;

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
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class GenericElasticRepository {

    private static RestHighLevelClient elasticClient = null;

    private static void setupConnection() {
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(
                AuthScope.ANY,
                new UsernamePasswordCredentials( "", "" )
        );

        RestClientBuilder restClient = RestClient.builder( new HttpHost( "127.0.0.1", 9200, "http" ) )
                .setHttpClientConfigCallback( ( HttpAsyncClientBuilder httpClientBuilder ) ->
                        httpClientBuilder.setDefaultCredentialsProvider( credentialsProvider ) );

        elasticClient = new RestHighLevelClient( restClient );
        try{
            elasticClient.ping( RequestOptions.DEFAULT );
        }
        catch(IOException e) {
            throw new RuntimeException((e));
        }
    }

    private static RestHighLevelClient getClient() {
        if (elasticClient == null) {
            setupConnection();
        }

        return elasticClient;
    }

    public List<String> getIdsFor(String index, String key, String value) {
        SearchRequest searchRequest = new SearchRequest(index);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.matchQuery(key, value).maxExpansions(10));
        searchRequest.source(searchSourceBuilder);

        List<String> ids = new ArrayList<>();

        SearchResponse response;
        try {
            response = getClient().search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        response.getHits().forEach(hit -> {
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String id = (String) sourceAsMap.get("zzCode");

            ids.add(id);
        });

        return ids;
    }

}

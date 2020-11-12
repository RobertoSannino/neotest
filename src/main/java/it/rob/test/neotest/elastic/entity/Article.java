package it.rob.test.neotest.elastic.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "text")
public class Article {

    @Id
    private String id;

    private String title;

    @Field("random_text")
    private String randomText;

}
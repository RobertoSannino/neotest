package it.rob.test.neotest.elastic.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;

@Data
@Document(indexName = "persone_fisiche")
public class PfElastic {

    @Id
    private String id;

    @Field("denominazione")
    private String denominazione;

    @Field("zzCode")
    private String zzCode;

    @Field("codiceFiscale")
    private String codiceFiscale;

}
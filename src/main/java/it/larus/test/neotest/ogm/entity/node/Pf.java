package it.larus.test.neotest.ogm.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NodeEntity
public class Pf {

    @Id
    @GeneratedValue
    private Long id;

    private String denominazione;
    private String zzCode;
    private String codiceFiscale;

    private Pf() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    @JsonIgnoreProperties({"personeFisiche"})
    @Relationship(type = "DI_COMPETENZA")
    private List<UfficioTerritoriale> ufficiTerritoriali = new ArrayList<>();

}

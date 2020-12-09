package it.larus.test.neotest.ogm.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@Data
@NodeEntity
public class UfficioProvinciale {

    @Id
    @GeneratedValue
    private Long id;

    @Property("ufficioProvinciale")
    private String name;

    private String zzCode;

    private UfficioProvinciale() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    @JsonIgnoreProperties({"personeFisiche", "ufficiProvinciali"})
    @Relationship(type = "APPARTIENE_A",direction = INCOMING)
    private List<UfficioTerritoriale> ufficiTerritoriali = new ArrayList<>();

}

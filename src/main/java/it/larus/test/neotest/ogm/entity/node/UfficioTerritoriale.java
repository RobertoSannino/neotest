package it.larus.test.neotest.ogm.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@Data
@NodeEntity
public class UfficioTerritoriale {

    @Id
    @GeneratedValue
    private Long id;

    private String ufficio;
    private String zzCode;

    private UfficioTerritoriale() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    @JsonIgnoreProperties({"ufficiTerritoriali"})
    @Relationship(type = "DI_COMPETENZA",direction = INCOMING)
    private List<Pf> personeFisiche = new ArrayList<>();

    @JsonIgnoreProperties({"ufficiTerritoriali"})
    @Relationship(type = "APPARTIENE_A")
    private List<UfficioProvinciale> ufficiProvinciali = new ArrayList<>();

}

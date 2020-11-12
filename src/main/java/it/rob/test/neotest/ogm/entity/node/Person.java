package it.rob.test.neotest.ogm.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.rob.test.neotest.ogm.entity.edge.ActedIn;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NodeEntity
public class Person {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @Property("born")
    private int birthyear;

    private Person() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public Person(String name) {
        this.name = name;
    }

    @JsonIgnoreProperties("person")
    @Relationship(type = "ACTED_IN")
    private List<ActedIn> actedIn = new ArrayList<>();

    @JsonIgnoreProperties({"actors", "directors"})
    @Relationship(type = "DIRECTED")
    private List<Movie> directed = new ArrayList<>();

}

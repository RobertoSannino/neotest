package it.rob.test.neotest.ogm.entity.node;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.rob.test.neotest.ogm.entity.edge.ActedIn;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.neo4j.ogm.annotation.Relationship.INCOMING;

@Data
@NodeEntity
public class Movie {

    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private int released;

    @Property("tagline")
    private String description;

    private Movie() {
        // Empty constructor required as of Neo4j API 2.0.5
    }

    public Movie(String title) {
        this.title = title;
    }

    @JsonIgnoreProperties("movie")
    @Relationship(type = "ACTED_IN", direction = INCOMING)
    private List<ActedIn> actors = new ArrayList<>();

    @JsonIgnoreProperties({"actedIn", "directed"})
    @Relationship(type = "DIRECTED", direction = INCOMING)
    private List<Person> directors = new ArrayList<>();


}

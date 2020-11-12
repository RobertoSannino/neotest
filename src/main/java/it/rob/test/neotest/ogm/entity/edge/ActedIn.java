package it.rob.test.neotest.ogm.entity.edge;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.rob.test.neotest.ogm.entity.node.Movie;
import it.rob.test.neotest.ogm.entity.node.Person;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Data
@RelationshipEntity(type = "ACTED_IN")
public class ActedIn {

    @Id
    @GeneratedValue
    private Long id;

    @Property("roles")
    private List<String> roles = new ArrayList<>();

    private ActedIn() {}

    @StartNode
    @JsonIgnoreProperties({"actedIn", "directed"})
    private Person person;

    @EndNode
    @JsonIgnoreProperties({"actors", "directors"})
    private Movie movie;
}

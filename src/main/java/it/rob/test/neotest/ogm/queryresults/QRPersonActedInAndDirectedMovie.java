package it.rob.test.neotest.ogm.queryresults;

import it.rob.test.neotest.ogm.entity.edge.ActedIn;
import it.rob.test.neotest.ogm.entity.node.Movie;
import it.rob.test.neotest.ogm.entity.node.Person;
import lombok.Data;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.neo4j.annotation.QueryResult;

@Data
@QueryResult
public class QRPersonActedInAndDirectedMovie {
    @Field("person")
    Person person;

    @Field("movie")
    Movie movie;

    @Field("actedIn")
    ActedIn actedIn;

    @Field("directed")
    Object directed;
}

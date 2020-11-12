package it.rob.test.neotest.ogm.repository;

import java.util.List;

import it.rob.test.neotest.ogm.entity.node.Person;
import it.rob.test.neotest.ogm.queryresults.QRPersonActedInAndDirectedMovie;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface PersonRepository extends Neo4jRepository<Person, Long> {

    Person getPersonByName(String name);

    Iterable<Person> findPersonByNameLike(String name);

    @Query("MATCH (m:Movie)<-[ai:ACTED_IN]-(p:Person)-[d:DIRECTED]->(m:Movie) return p as person, ai as actedIn, d as directed, m as movie")
    List<QRPersonActedInAndDirectedMovie> getPersonsWhoActAndDirectAMovieQR();

    @Query("MATCH (p:Person) WHERE p.name in $namesList RETURN p;")
    List<Person> findByNameInList(@Param("namesList") List<String> namesList);
}
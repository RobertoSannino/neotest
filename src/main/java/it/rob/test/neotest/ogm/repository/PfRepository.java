package it.rob.test.neotest.ogm.repository;

import it.rob.test.neotest.ogm.entity.node.Pf;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface PfRepository extends Neo4jRepository<Pf, Long> {

    Pf getPfByCodiceFiscale(String cf);
/*
    Iterable<Person> findPersonByNameLike(String name);

    @Query("MATCH (m:Movie)<-[ai:ACTED_IN]-(p:Person)-[d:DIRECTED]->(m:Movie) return p as person, ai as actedIn, d as directed, m as movie")
    List<QRPersonActedInAndDirectedMovie> getPersonsWhoActAndDirectAMovieQR();

    @Query("MATCH (p:Person) WHERE p.name in $namesList RETURN p;")
    List<Person> findByNameInList(@Param("namesList") List<String> namesList);
    */

}
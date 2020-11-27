package it.rob.test.neotest.ogm.repository;

import it.rob.test.neotest.ogm.entity.node.Pf;
import it.rob.test.neotest.ogm.queryresults.QRUfficiPf;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PfRepository extends Neo4jRepository<Pf, Long> {

    Pf getPfByCodiceFiscale(String cf);

    @Query(
            "MATCH (pf:Pf) WHERE pf.codiceFiscale in $denomList \n" +
            "MATCH path=(up:UfficioProvinciale )<-[to_up:APPARTIENE_A]-(ut:UfficioTerritoriale)<-[to_ut*1..1]-(pf)  \n" +
            "RETURN pf, up, ut,to_up, to_ut"
    )
    List<QRUfficiPf> findUfficiByPfNames(@Param("denomList") List<String> denomList);
/*
    Iterable<Person> findPersonByNameLike(String name);

    @Query("MATCH (m:Movie)<-[ai:ACTED_IN]-(p:Person)-[d:DIRECTED]->(m:Movie) return p as person, ai as actedIn, d as directed, m as movie")
    List<QRPersonActedInAndDirectedMovie> getPersonsWhoActAndDirectAMovieQR();

    @Query("MATCH (p:Person) WHERE p.name in $namesList RETURN p;")
    List<Person> findByNameInList(@Param("namesList") List<String> namesList);
    */

}
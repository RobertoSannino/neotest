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

    // TODO queryLogic, label, depth
    @Query(
        "UNWIND $cfList as condition " +
        "WITH 'MATCH (pf:Pf {codiceFiscale: condition }) MATCH (up:UfficioProvinciale )<-[to_up:APPARTIENE_A]-(ut:UfficioTerritoriale)<-[to_ut*1..1]-(pf)  RETURN pf,to_ut,ut,to_up,up' as query, condition \n" +
        "CALL apoc.cypher.run(query,{condition:condition}) YIELD value AS v\n" +
        "RETURN v.pf as pf, v.up as up, v.ut as ut, v.to_up as to_up, v.to_ut as to_ut"
    )
    List<QRUfficiPf> findUfficiByQueries(@Param("cfList") List<String> cfList, @Param("queryLogic") String queryLogic);

}
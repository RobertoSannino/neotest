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
        "UNWIND $cfList as condition \n" +
        "WITH 'MATCH (pf:Pf {codiceFiscale: \"' + condition + '\"}) MATCH p = (up:UfficioProvinciale )<-[to_up:APPARTIENE_A]-(ut:UfficioTerritoriale)<-[to_ut*1..1]-(pf)  RETURN p' as query, condition \n" +
        "CALL apoc.cypher.run(query,{}) YIELD value \n" +
        "WITH CASE WHEN size(collect(value.p)) = size($cfList) OR \"or\" = $queryLogic THEN value.p ELSE null END as v \n" +
        "RETURN v"
    )
    List<Object> findUfficiByQueries(@Param("cfList") List<String> cfList, @Param("queryLogic") String queryLogic);

}
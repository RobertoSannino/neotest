package it.larus.test.neotest.ogm.repository;

import it.larus.test.neotest.api.v2.NodeQuery;
import it.larus.test.neotest.util.ElasticUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.neo4j.driver.Record;
import org.neo4j.driver.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.neo4j.driver.Values.parameters;
import static org.springframework.util.CollectionUtils.isEmpty;

@Slf4j
public class GenericNeo4jRepository {

    private final Driver driver;

    public GenericNeo4jRepository() {
        driver = GraphDatabase.driver( "bolt://localhost:7687", AuthTokens.basic( "neo4j", "secret" ) );
    }

    public List<Map<String, Object>> runCypherQuery(String query ) {
        List<Map<String, Object>> results = new ArrayList<>();

        try (Session session = driver.session()) {
            results = session.readTransaction(tx -> {
                Result queryResult = tx.run(
                        query,
                        parameters("message", query));

                return queryResult.list().stream().map(Record::asMap).collect(Collectors.toList());
            });
            results.forEach(r -> log.info(r.toString()));
        }

        return results;
    }

    public String generateMatchPath(String pathName, NodeQuery startNode, NodeQuery endNode, String relLabel, int maxDepth) {
        HashMap<String, String> parameters = new HashMap<>();
        String matchPath =
                "MATCH ${pathName} = (${startName}:${startLabel})-[${relLabel}]->(${endName}:${endLabel}) ";

        if (!isEmpty(startNode.getIdsXonar()) || !isEmpty(endNode.getIdsXonar()))
            matchPath = matchPath + "WHERE ";

        if (!isEmpty(startNode.getIdsXonar()) && !isEmpty(endNode.getIdsXonar())) {
            matchPath = matchPath + "${startName}.${startId} IN ${listStartNodeIds} AND ${endName}.${endId} IN ${listEndNodeIds}";
            parameters.put("listStartNodeIds", startNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());
            parameters.put("listEndNodeIds", endNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());
        } else if (!isEmpty(startNode.getIdsXonar())) {
            matchPath = matchPath + "${startName}.${startId} IN ${listStartNodeIds}";
            parameters.put("listStartNodeIds", startNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());
        } else if (!isEmpty(endNode.getIdsXonar())) {
            matchPath = matchPath + "${endName}.${endId} IN ${listEndNodeIds}";
            parameters.put("listEndNodeIds", endNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());
        }

        parameters.put("pathName", pathName);
        parameters.put("startName", startNode.getId());
        parameters.put("startLabel", startNode.getLabel());
        parameters.put("relLabel",  nonNull(relLabel) ? ":" + relLabel : "*0.." + maxDepth);
        parameters.put("endName", endNode.getId());
        parameters.put("endLabel", endNode.getLabel());
        parameters.put("startId", ElasticUtils.getIdNameForLabel(startNode.getLabel()));
        // parameters.put("listStartNodeIds", startNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());
        parameters.put("endId", ElasticUtils.getIdNameForLabel(endNode.getLabel()));
        // parameters.put("listEndNodeIds", endNode.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());

        StringSubstitutor sub = new StringSubstitutor(parameters);
        return sub.replace(matchPath);
    }

}

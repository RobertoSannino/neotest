package it.larus.test.neotest.util;

import it.larus.test.neotest.api.v2.RelQuery;
import it.larus.test.neotest.api.v3.v2.NodeQueryV3Api;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Slf4j
public class ExpandPathUtil {

    private ExpandPathUtil() {}

    public static String generateExpandPathQuery (QueryV3Api queryV3Api) {
        String matchPaths = generateMatchPath(queryV3Api.getNodeQueries());
        String expandPaths = generateExpandPath(queryV3Api.getNodeQueries(), queryV3Api.getRelQueries());
        return "\n" + matchPaths + expandPaths + "RETURN " + queryV3Api.getReturnCond();
    }

    private static final String MATCH_PROTOTYPE = "MATCH (${startName}:${startLabel}) WHERE ${startName}.${startId} IN ${listStartNodeIds}";

    private static String generateMatchPath(List<NodeQueryV3Api> nodeQueries) {
        StringBuilder matchPaths = new StringBuilder("");
        for (NodeQueryV3Api nq : nodeQueries) {
            if (nonNull(nq.getQuery())) {
                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("startName", nq.getId());
                parameters.put("startLabel", nq.getLabel());
                parameters.put("startId", ElasticUtils.getIdNameForLabel(nq.getLabel()));
                parameters.put("listStartNodeIds", nq.getIdsXonar().stream().map(ids -> "'"+ids+"'").collect(Collectors.toList()).toString());

                StringSubstitutor sub = new StringSubstitutor(parameters);
                matchPaths.append(sub.replace(MATCH_PROTOTYPE)).append(" \n");
            }
        }
        return matchPaths.toString();
    }

    private static final String EXPAND_PROTOTYPE =
            "CALL apoc.path.expandConfig(${startNode}, {\n" +
                    "    relationshipFilter: '${relFilter}',\n" +
                    "    labelFilter: '/${termLabelFilter}',\n" + // -${blackLabelFilter}
                    "    minLevel: ${minLevel},\n" +
                    "    maxLevel: ${maxLevel}\n" +
                    "})\n" +
                    "YIELD path as ${pathName}";

    private static String generateExpandPath(List<NodeQueryV3Api> nodeQueries, List<RelQuery> relQueries) {
        relQueries.sort(Comparator.comparing(RelQuery::getStart));
        StringBuilder expandPaths = new StringBuilder("");

        for (int i = 0; i < relQueries.size(); i++) {
            RelQuery rq = relQueries.get(i);

            HashMap<String, String> parameters = new HashMap<>();
            // si fà l'assunzione che almeno lo start node sia stato definito con una query, secondo me và messo quasi come vincolo altrimenti usi neo4j e ciao
            parameters.put("startNode", i == 0 ?
                    nodeQueries.stream().filter(nq -> nq.getId().equals(rq.getStart())).findFirst().get().getId() :
                    "LAST(NODES(" + relQueries.get(i-1).getId() + "))");
            parameters.put("relFilter", nonNull(rq.getLabel()) ? rq.getLabel() + ">" : ">");
            parameters.put("termLabelFilter", nodeQueries.stream().filter(nq -> nq.getId().equals(rq.getEnd())).findFirst().get().getLabel());
            parameters.put("minLevel", "1");
            parameters.put("maxLevel", String.valueOf(Math.max(1, rq.getMaxDepth())));
            parameters.put("pathName", rq.getId());

            StringSubstitutor sub = new StringSubstitutor(parameters);
            expandPaths.append(sub.replace(EXPAND_PROTOTYPE)).append(" \n");
        }

        return expandPaths.toString();
    }
}

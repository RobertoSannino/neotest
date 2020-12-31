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

    private final Set<String> declaredVariables = new HashSet<>();
    private final Set<String> declaredPaths = new HashSet<>();

    private boolean isVariableDeclared(String variableName) {
        return declaredVariables.contains(variableName);
    }

    private void declareVariable(String variableName) {
        declaredVariables.add(variableName);
    }

    private void declarePath(String pathName) {
        declaredPaths.add(pathName);
    }

    private String getDeclarations() {
        Set<String> union = new HashSet<>(this.declaredVariables);
        union.addAll(this.declaredPaths);

        return String.join(",", union);
    }

    public String generateExpandPathQuery(QueryV3Api queryV3Api) {
        String matchPaths = generateMatchPath(queryV3Api.getNodeQueries());
        String expandPaths = generateExpandPath(queryV3Api.getNodeQueries(), queryV3Api.getRelQueries());
        return "\n" + matchPaths + expandPaths + "RETURN " + queryV3Api.getReturnCond();
    }

    private static final String MATCH_PROTOTYPE = "MATCH (${startName}:${startLabel}) WHERE ${startName}.${startId} IN ${listStartNodeIds}";

    private String generateMatchPath(List<NodeQueryV3Api> nodeQueries) {
        StringBuilder matchPaths = new StringBuilder("");
        for (NodeQueryV3Api nq : nodeQueries) {
            if (nonNull(nq.getQuery())) {
                // Add to declared variables as it is generated via a match statement
                declaredVariables.add(nq.getId());

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

    private static final String EXPAND_PROTOTYPE_TERMINATION_LABELS =
            "CALL apoc.path.expandConfig(${startNode}, {\n" +
                    "    relationshipFilter: '${relFilter}',\n" +
                    "    labelFilter: '>${termLabelFilter}',\n" + // -${blackLabelFilter}
                    "    minLevel: ${minLevel},\n" +
                    "    maxLevel: ${maxLevel}\n" +
                    "})\n" +
                    "YIELD path as ${pathName}\n" +
                    "WITH LAST(NODES(${pathName})) AS ${endNode}\n";

    private static final String EXPAND_PROTOTYPE_TERMINATION_NODES =
            "CALL apoc.path.expandConfig(${startNode}, {\n" +
                    "    relationshipFilter: '${relFilter}',\n" +
                    "    labelFilter: '',\n" + // -${blackLabelFilter}
                    "    minLevel: ${minLevel},\n" +
                    "    maxLevel: ${maxLevel},\n" +
                    "    terminationNodes: [${endNode}]\n" +
                    "})\n" +
                    "YIELD path as ${pathName}";

    private String generateExpandPath(List<NodeQueryV3Api> nodeQueries, List<RelQuery> relQueries) {
        StringBuilder expandPaths = new StringBuilder("");

        for (int i = 0; i < relQueries.size(); i++) {
            RelQuery rq = relQueries.get(i);

            // Declare current path
            declarePath(rq.getId());

            String endVariableName = rq.getEnd();
            String endVariableLabel = nodeQueries.stream().filter(nq -> nq.getId().equals(endVariableName)).findFirst().get().getLabel();
            boolean endVariableDeclared = isVariableDeclared(endVariableName);

            HashMap<String, String> parameters = new HashMap<>();
            // si fà l'assunzione che almeno lo start node sia stato definito con una query, secondo me và messo quasi come vincolo altrimenti usi neo4j e ciao
            parameters.put("startNode", rq.getStart());
            parameters.put("relFilter", nonNull(rq.getLabel()) ? rq.getLabel() + ">" : ">");

            if (!endVariableDeclared) {
                parameters.put("termLabelFilter", endVariableLabel);
            }
            parameters.put("endNode", endVariableName);
            parameters.put("minLevel", "1");
            parameters.put("maxLevel", String.valueOf(Math.max(1, rq.getMaxDepth())));
            parameters.put("pathName", rq.getId());

            StringSubstitutor sub = new StringSubstitutor(parameters);
            String prototype = EXPAND_PROTOTYPE_TERMINATION_NODES;
            if (!endVariableDeclared) {
                String declarations = getDeclarations();
                declarations = declarations.length() != 0 ? ", " + declarations : "";
                prototype = EXPAND_PROTOTYPE_TERMINATION_LABELS + declarations + "\n";
            }
            expandPaths.append(sub.replace(prototype)).append(" \n");

            if (!endVariableDeclared) {
                declareVariable(endVariableName);
            }
        }

        return expandPaths.toString();
    }
}

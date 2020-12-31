package it.larus.test.neotest.util;

import it.larus.test.neotest.api.v2.RelQuery;
import it.larus.test.neotest.api.v3.v2.NodeQueryV3Api;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;

@Slf4j
public class PathExpander {

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

        return String.join(", ", union);
    }

    public String generateExpandPathQuery(QueryV3Api queryV3Api, List<String> groups) {
        String matchPaths = generateMatchPath(queryV3Api.getNodeQueries(), groups);
        String expandPaths = generateExpandPath(queryV3Api.getNodeQueries(), queryV3Api.getRelQueries(), groups);
        return "\n" + matchPaths + expandPaths + "RETURN " + queryV3Api.getReturnCond();
    }

    private static final String MATCH_PROTOTYPE = "MATCH (${startName}:${startLabel}${groupLabels}) WHERE ${startName}.${startId} IN ${listStartNodeIds}";

    private String generateMatchPath(List<NodeQueryV3Api> nodeQueries, List<String> groups) {
        StringBuilder matchPaths = new StringBuilder("");
        for (NodeQueryV3Api nq : nodeQueries) {
            if (nonNull(nq.getQuery())) {
                // Add to declared variables as it is generated via a match statement
                declaredVariables.add(nq.getId());

                HashMap<String, String> parameters = new HashMap<>();
                parameters.put("startName", nq.getId());
                parameters.put("startLabel", nq.getLabel());
                parameters.put("groupLabels", emptyIfNull(groups).stream().map(g -> ":" + g).reduce((g1,g2) -> g1 + g2).orElse(""));
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
                    "    labelFilter: '+${whitelistLabels}|>${termLabelFilter}',\n" +
                    "    minLevel: ${minLevel},\n" +
                    "    maxLevel: ${maxLevel},\n" +
                    "    optional: ${optional}\n" +
                    "})\n" +
                    "YIELD path as ${pathName}\n" +
                    "WITH LAST(NODES(${pathName})) AS ${endNode}";

    private static final String EXPAND_PROTOTYPE_TERMINATION_NODES =
            "CALL apoc.path.expandConfig(${startNode}, {\n" +
                    "    relationshipFilter: '${relFilter}',\n" +
                    "    labelFilter: '+${whitelistLabels}',\n" +
                    "    minLevel: ${minLevel},\n" +
                    "    maxLevel: ${maxLevel},\n" +
                    "    optional: ${optional},\n" +
                    "    terminationNodes: [${endNode}]\n" +
                    "})\n" +
                    "YIELD path as ${pathName}";

    private String generateExpandPath(List<NodeQueryV3Api> nodeQueries, List<RelQuery> relQueries, List<String> groups) {
        StringBuilder expandPaths = new StringBuilder("");

        for (int i = 0; i < relQueries.size(); i++) {
            RelQuery rq = relQueries.get(i);

            // Declare current path
            declarePath(rq.getId());

            String endVariableName = rq.getEnd();
            String endVariableLabel = nodeQueries.stream().filter(nq -> nq.getId().equals(endVariableName)).findFirst().get().getLabel();
            boolean endVariableDeclared = isVariableDeclared(endVariableName);

            String whitelistLabels = emptyIfNull(groups).stream().map(g -> g + ":").reduce((g1, g2) -> g1 + g2).orElse("");
            whitelistLabels = whitelistLabels.equals("") ? "" : whitelistLabels.substring(0, whitelistLabels.lastIndexOf(":"));

            HashMap<String, String> parameters = new HashMap<>();
            // si fà l'assunzione che almeno lo start node sia stato definito con una query, secondo me và messo quasi come vincolo altrimenti usi neo4j e ciao
            parameters.put("startNode", rq.getStart());
            parameters.put("relFilter", nonNull(rq.getLabel()) ? rq.getLabel() + ">" : ">");

            if (!endVariableDeclared) {
                parameters.put("termLabelFilter", endVariableLabel + ":" + whitelistLabels);
            }
            parameters.put("endNode", endVariableName);
            parameters.put("whitelistLabels", whitelistLabels);
            parameters.put("minLevel", String.valueOf(rq.getMinDepth()));
            parameters.put("maxLevel", String.valueOf(Math.max(1, rq.getMaxDepth())));
            parameters.put("optional", String.valueOf(rq.isOptional()));
            parameters.put("pathName", rq.getId());

            StringSubstitutor sub = new StringSubstitutor(parameters);
            String prototype = EXPAND_PROTOTYPE_TERMINATION_NODES;
            if (!endVariableDeclared) {
                String declarations = getDeclarations();
                declarations = declarations.length() != 0 ? ", " + declarations : "\n";
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

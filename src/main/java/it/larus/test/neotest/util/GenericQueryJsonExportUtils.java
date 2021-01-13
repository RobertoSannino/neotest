package it.larus.test.neotest.util;

import org.neo4j.driver.Record;
import org.neo4j.driver.Result;
import org.neo4j.driver.Value;
import org.neo4j.driver.internal.InternalNode;
import org.neo4j.driver.internal.InternalPath;
import org.neo4j.driver.internal.InternalRelationship;
import org.neo4j.driver.internal.value.*;
import org.neo4j.driver.types.Path;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GenericQueryJsonExportUtils {

    private static final String RESULT_ARRAY_WRAPPER =
            "CALL apoc.export.json.query(\"%s\", null, {stream: true, format: 'ARRAY_JSON'}) YIELD data\n" +
                    "RETURN data";

    // TODO NODES_AND_RELATIONSHIP WRAPPER

    public enum ExportMode {
        RESULT_ARRAY,
        NODES_AND_RELATIONSHIPS
    }

    private GenericQueryJsonExportUtils() {}

    public static String wrapWithExport(String query, ExportMode mode) {
        if (mode == ExportMode.RESULT_ARRAY) {
            query = query.replace("\"", "\\\\\""); // escape quotes in provided query

            return String.format(RESULT_ARRAY_WRAPPER, query);
        }

        throw new UnsupportedOperationException(ExportMode.NODES_AND_RELATIONSHIPS.name() + " not currently supported");
    }

    public static void fillOutputMap(Result result, Map<String, Node> nodesRes, Map<String, Map<String, Relationship>> rels, Map<String, String> cons) {
        while (result.hasNext()) {
            Record record = result.next();

            record.keys().forEach(k -> {
                Value v = record.get(k);
                if (v instanceof StringValue) {
                    cons.put(k, v.asString());
                }
                else if (v instanceof ListValue) {
                    v.asList().forEach( v_in_list ->
                            node_or_relation( v_in_list, nodesRes, rels )
                    );
                }
                else{
                    node_or_relation( v, nodesRes, rels );
                }

            });
        }

    }

    private static void node_or_relation(Object v, Map<String, Node> nodesRes, Map<String, Map<String, Relationship>> rels) {

        if (v instanceof NodeValue) {
            nodesRes.put(
                    Long.toString(((NodeValue) v).asNode().id()),
                    new Node(
                            Long.toString(((NodeValue) v).asNode().id()),
                            StreamSupport
                                    .stream(((NodeValue) v).asNode().labels().spliterator(), false)
                                    .collect(Collectors.toList()),
                            ((NodeValue) v).asNode().asMap(),
                            (int) ((NodeValue) v).asNode().asMap().getOrDefault("degree", 0)
                    )
            );
        }
        else if (v instanceof InternalNode) {
            nodesRes.put(
                    Long.toString(((InternalNode) v).id()),
                    new Node(
                            Long.toString(((InternalNode) v).id()),
                            new ArrayList<>(
                                    ((InternalNode) v).labels()
                            ),
                            ((InternalNode) v).asMap(),
                            (int)(((InternalNode) v).asMap().getOrDefault("degree", 0))
                    )
            );
        }
        else if (v instanceof RelationshipValue) {
            Relationship relation = new Relationship(
                    Long.toString(((RelationshipValue) v).asRelationship().id()),
                    ((RelationshipValue) v).asRelationship().type(),
                    ((RelationshipValue) v).asRelationship().asMap()
//                    new Node( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()) )
//                    nodesRes.getOrDefault(
//                            Long.toString(((RelationshipValue) v).asRelationship().startNodeId()),
//                            new util.domain.Node(
//                                    Long.toString(((RelationshipValue) v).asRelationship().startNodeId())
//                            )
//                    )
//                    ,
//                    new Node( Long.toString(((RelationshipValue) v).asRelationship().endNodeId()) )
//                    nodesRes.getOrDefault(
//                            Long.toString(((RelationshipValue) v).asRelationship().endNodeId()),
//                            new util.domain.Node(
//                                    Long.toString(((RelationshipValue) v).asRelationship().endNodeId())
//                            )
//                    )
            );
            if( !rels.containsKey( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()) ) ){
                rels.put( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()), new HashMap<>() );
                rels.get( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()) )
                        .put(
                                Long.toString(((RelationshipValue) v).asRelationship().endNodeId() ),
                                relation
                        );
            }
            else if( !rels.get( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()) ).containsKey( Long.toString(((RelationshipValue) v).asRelationship().endNodeId() ) ) ){
                rels.get( Long.toString(((RelationshipValue) v).asRelationship().startNodeId()) )
                        .put(
                                Long.toString(((RelationshipValue) v).asRelationship().endNodeId() ),
                                relation
                        );
            }
        }
        else if (v instanceof InternalRelationship) {
            Relationship relation = new Relationship(
                    Long.toString(((InternalRelationship) v).id()),
                    ((InternalRelationship) v).type(),
                    ((InternalRelationship)v).asMap(),
                    new Node( Long.toString(((InternalRelationship) v).startNodeId()) ),
                    new Node( Long.toString(((InternalRelationship) v).endNodeId()) )
                    /*nodesRes.getOrDefault(
                            Long.toString(((InternalRelationship) v).startNodeId()),
                            new Node(
                                    Long.toString(((InternalRelationship) v).startNodeId())
                            )
                    ),
                    nodesRes.getOrDefault(
                            Long.toString(((InternalRelationship) v).endNodeId()),
                            new Node(
                                    Long.toString(((InternalRelationship) v).endNodeId())
                            )
                    )*/
            );

            if( !rels.containsKey( Long.toString(((InternalRelationship) v).startNodeId()) ) ) {
                rels.put( Long.toString(((InternalRelationship) v).startNodeId()), new HashMap<>() );
                rels.get( Long.toString(((InternalRelationship) v).startNodeId()) )
                        .put(
                                Long.toString(((InternalRelationship) v).endNodeId() ),
                                relation
                        );
            }
            else if( !rels.get( Long.toString(((InternalRelationship) v).startNodeId()) ).containsKey( Long.toString(((InternalRelationship) v).endNodeId() ) ) ) {
                rels.get( Long.toString(((InternalRelationship) v).startNodeId()) )
                        .put(
                                Long.toString(((InternalRelationship) v).endNodeId() ),
                                relation
                        );
            }

        }
        else if( v instanceof PathValue){
            Path path = ((PathValue) v).asPath();
            for (org.neo4j.driver.types.Node node:path.nodes()) {
                node_or_relation( node, nodesRes, rels );
            }
            for (org.neo4j.driver.types.Relationship rel:path.relationships()) {
                node_or_relation( rel, nodesRes, rels );
            }
        }

        else if (v instanceof InternalPath) {
            InternalPath internalPath = (InternalPath) v;
            internalPath.nodes().forEach(n -> node_or_relation(n, nodesRes, rels));
            internalPath.relationships().forEach(r -> node_or_relation(r, nodesRes, rels));
        }

    }

}

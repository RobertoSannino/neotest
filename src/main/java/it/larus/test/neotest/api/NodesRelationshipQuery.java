package it.larus.test.neotest.api;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NodesRelationshipQuery {

    @Data
    public static class NodeQuery {
        private String label;
        private Map<String, String> propertyNameToValue;
    }

    private List<NodeQuery> nodeQueries;
}

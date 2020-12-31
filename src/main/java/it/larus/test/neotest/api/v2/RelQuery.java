package it.larus.test.neotest.api.v2;

import lombok.Data;

import java.util.List;

@Data
public class RelQuery {

    String id;
    String label;
    String start;
    String end;
    Integer minDepth = null;
    Integer maxDepth = null;
    boolean optional = false;
    List<QueryConstraint> constraints;

}

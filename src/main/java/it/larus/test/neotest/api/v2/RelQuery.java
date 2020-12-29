package it.larus.test.neotest.api.v2;

import lombok.Data;

import java.util.List;

@Data
public class RelQuery {

    String id;
    String label;
    String start;
    String end;
    int maxDepth;
    List<QueryConstraint> constraints;

}

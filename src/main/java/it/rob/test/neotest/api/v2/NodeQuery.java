package it.rob.test.neotest.api.v2;

import lombok.Data;

import java.util.List;

@Data
public class NodeQuery {

    long id;

    List<String> idsXonar;
    boolean idsXonarRequired = true;

    String label;
    List<QueryContraint> constraints;

}

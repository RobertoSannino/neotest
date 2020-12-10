package it.rob.test.neotest.api.v2;

import lombok.Data;

import java.util.List;

@Data
public class RelQuery {

    String label;
    long start;
    long end;
    List<QueryContraint> constraints;

}

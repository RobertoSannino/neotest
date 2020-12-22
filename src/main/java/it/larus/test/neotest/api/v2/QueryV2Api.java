package it.larus.test.neotest.api.v2;

import lombok.Data;

import java.util.List;

@Data
public class QueryV2Api {

    List<NodeQuery> nodeQueries;
    List<RelQuery> relQueries;

}

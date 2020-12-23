package it.larus.test.neotest.api.v3.v2;

import it.larus.test.neotest.api.v2.RelQuery;
import lombok.Data;

import java.util.List;

@Data
public class QueryV3Api {

    List<NodeQueryV3Api> nodeQueries;
    List<RelQuery> relQueries;
    List<OrderByNode> orderByNodes;

}

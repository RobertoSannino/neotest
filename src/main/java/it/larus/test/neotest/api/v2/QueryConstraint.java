package it.larus.test.neotest.api.v2;

import it.larus.test.neotest.constant.SearchType;
import lombok.Data;

@Data
public class QueryConstraint {

    String key;
    String value;
    SearchType searchType;
}

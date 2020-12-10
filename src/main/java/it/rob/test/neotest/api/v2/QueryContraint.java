package it.rob.test.neotest.api.v2;

import it.rob.test.neotest.constant.SearchType;
import lombok.Data;

@Data
public class QueryContraint {

    String key;
    String value;
    SearchType searchType;
}

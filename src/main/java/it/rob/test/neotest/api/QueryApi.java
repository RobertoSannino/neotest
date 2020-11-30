package it.rob.test.neotest.api;

import it.rob.test.neotest.constant.SearchType;
import lombok.Data;

@Data
public class QueryApi {

    String key;
    String value;
    SearchType searchType;
    String label;

}

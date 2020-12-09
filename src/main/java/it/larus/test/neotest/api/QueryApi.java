package it.larus.test.neotest.api;

import it.larus.test.neotest.constant.SearchType;
import lombok.Data;

@Data
public class QueryApi {

    String key;
    String value;
    SearchType searchType;
    String label;

}

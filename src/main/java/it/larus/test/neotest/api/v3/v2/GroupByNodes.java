package it.larus.test.neotest.api.v3.v2;

import lombok.Data;

import java.util.List;

@Data
public class GroupByNodes {
    @Data
    public static class Aggregated {
        private String field;
        private String operand;
    }

    private List<String> aggr;
    private List<Aggregated> aggregated;
}

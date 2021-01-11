package it.larus.test.neotest.api.v3.v2;

import lombok.Data;

import java.util.List;

@Data
public class GroupByNodes {
    @Data
    public static class Aggregated {
        private String id;
        private String property;
        private String operand;
        private String alias;
    }

    @Data
    public static class Aggregation {
        private String id;
        private String property;
    }

    private List<Aggregation> by;
    private List<Aggregated> aggregate;
}

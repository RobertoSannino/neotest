package it.larus.test.neotest.validator;

import it.larus.test.neotest.api.v2.RelQuery;
import it.larus.test.neotest.api.v3.v2.GroupByNodes;
import it.larus.test.neotest.api.v3.v2.NodeQueryV3Api;
import it.larus.test.neotest.api.v3.v2.OrderByNode;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import it.larus.test.neotest.exception.BadRequestException;

import java.util.regex.Pattern;

import static java.util.Objects.nonNull;

public class InputQueryValidator {

    private static Pattern variablePattern = Pattern.compile("^[a-zA-Z0-9]+$");

    private static void isValid(String val) {
        // Null strings are accepted, another validator should be responsible
        // for checking whether a null value is allowed.
        if (nonNull(val) && !variablePattern.matcher(val).find()) {
            throw new BadRequestException("Variable or label " + val + " must be a single non-empty alphanumeric word.");
        }
    }

    private static void isValid(NodeQueryV3Api nodeQuery) {
        isValid(nodeQuery.getId());
        isValid(nodeQuery.getLabel());
    }

    public static void isValid(RelQuery relQuery) {
        isValid(relQuery.getId());
        isValid(relQuery.getStart());
        isValid(relQuery.getEnd());
    }

    public static void isValid(OrderByNode orderByNode) {
        isValid(orderByNode.getId());
        isValid(orderByNode.getAttr());
    }

    public static void isValid(GroupByNodes groupByNodes) {
        groupByNodes.getAggr().forEach(InputQueryValidator::isValid);
        groupByNodes.getAggregated().forEach(InputQueryValidator::isValid);
    }

    private static void isValid(GroupByNodes.Aggregated aggregated) {
        isValid(aggregated.getField());
        isValid(aggregated.getOperand()); // TODO check if the operand belongs to a set of allowed operands
    }

    public static void isValid(QueryV3Api inputQuery) {
        inputQuery.getNodeQueries().forEach(InputQueryValidator::isValid);
        inputQuery.getRelQueries().forEach(InputQueryValidator::isValid);
        inputQuery.getOrderByNodes().forEach(InputQueryValidator::isValid);
        isValid(inputQuery.getGroupByNode());
    }

}

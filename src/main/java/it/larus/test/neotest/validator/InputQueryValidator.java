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
        if (nonNull(nodeQuery)) {
            isValid(nodeQuery.getId());
            isValid(nodeQuery.getLabel());
        }
    }

    public static void isValid(RelQuery relQuery) {
        if (nonNull(relQuery)) {
            isValid(relQuery.getId());
            isValid(relQuery.getStart());
            isValid(relQuery.getEnd());
        }
    }

    public static void isValid(OrderByNode orderByNode) {
        if (nonNull(orderByNode)) {
            isValid(orderByNode.getId());
            isValid(orderByNode.getAttr());
        }
    }

    public static void isValid(GroupByNodes groupByNodes) {
        if (nonNull(groupByNodes)) {
            groupByNodes.getBy().forEach(InputQueryValidator::isValid);
            groupByNodes.getAggregate().forEach(InputQueryValidator::isValid);
        }
    }

    private static void isValid(GroupByNodes.Aggregated aggregated) {
        if (nonNull(aggregated)) {
            isValid(aggregated.getProperty());
            isValid(aggregated.getOperand()); // TODO check if the operand belongs to a set of allowed operands
            isValid(aggregated.getAlias());
        }
    }

    private static void isValid(GroupByNodes.Aggregation aggregation) {
        if (nonNull(aggregation)) {
            isValid(aggregation.getProperty());
            isValid(aggregation.getId());
        }
    }

    public static void isValid(QueryV3Api inputQuery) {
        if (nonNull(inputQuery.getNodeQueries())) {
            inputQuery.getNodeQueries().forEach(InputQueryValidator::isValid);
        }

        if (nonNull(inputQuery.getRelQueries())) {
            inputQuery.getRelQueries().forEach(InputQueryValidator::isValid);
        }
        if (nonNull(inputQuery.getOrderByNodes())) {
            inputQuery.getOrderByNodes().forEach(InputQueryValidator::isValid);
        }
        isValid(inputQuery.getGroupByNode());
    }
}

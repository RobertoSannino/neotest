package it.larus.test.neotest.validator;

import it.larus.test.neotest.api.v3.v2.NodeQueryV3Api;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import it.larus.test.neotest.exception.BadRequestException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;


public class ParamValidator {

    private ParamValidator() {}

    public static void checkCodiceFiscale (String codiceFiscale) {
        if (codiceFiscale.length() != 16) {
            throw new BadRequestException("CF must be exactly 16 chars long");
        }
    }

    public static void checkPageRequest(int pageNumber, int numberOfResults) {
        if (pageNumber < 0 || numberOfResults < 0) {
            throw new BadRequestException("Page number and number of results must be greater than zero");
        }
    }

    public static void validateQueryV3Api(QueryV3Api queryV3Api) {
        // check non-empty nodeQueries and relQueries
        if (isEmpty(queryV3Api.getNodeQueries()) || isEmpty(queryV3Api.getRelQueries())) {
            throw new BadRequestException("NodeQueries and RelQueries are mandatory");
        }
        // check nodeQueries have labels
        if (queryV3Api.getNodeQueries().stream().anyMatch(nq -> isNull(nq.getLabel()))) {
            throw new BadRequestException("NodeQueries \"labels\" are mandatory");
        }
        // check relQueries label and depth mutually-exclusive
        if (queryV3Api.getRelQueries().stream().anyMatch(rq ->
                (isNull(rq.getLabel()) && (isNull(rq.getMaxDepth()) && isNull(rq.getMinDepth()))) ||
                (nonNull(rq.getLabel()) && (nonNull(rq.getMaxDepth()) || nonNull(rq.getMinDepth()))))
        ) {
            throw new BadRequestException("RelQueries \"label\" and \"maxDepth\" | \"minDepth\" are mutually exclusive, populate one of them");
        }
    }

    public static void validateQueryV3Api_ExtendVersion(QueryV3Api queryV3Api, List<String> groups) {
        validateQueryV3Api(queryV3Api);

        // check min and maxDepth
        if (queryV3Api.getRelQueries().stream().anyMatch(rq -> nonNull(rq.getMinDepth()) && nonNull(rq.getMaxDepth()) && rq.getMinDepth() > rq.getMaxDepth())) {
            throw new BadRequestException("\"maxDepth\" must be greater or equal than \"minDepth\"");
        }
        // check non-empty groups
        if (isEmpty(groups)) {
            throw new BadRequestException("Groups must be provided in order to perform a query");
        }
        // check starting node for expand has query
        Optional<NodeQueryV3Api> firstStartNode = queryV3Api.getNodeQueries().stream().filter(nq -> nq.getId().equals(queryV3Api.getRelQueries().get(0).getStart())).findFirst();
        if (!firstStartNode.isPresent() || isNull(firstStartNode.get().getQuery())) {
            throw new BadRequestException("Starting node must have \"query\" populated");
        }
        // check nodes without query are not returned
        Stream<NodeQueryV3Api> nodeQueryWithoutQuery = queryV3Api.getNodeQueries().stream().filter(nq -> isNull(nq.getQuery()));
        if (nodeQueryWithoutQuery.anyMatch(nq -> queryV3Api.getReturnCond().contains(nq.getId() + ","))) {
            throw new BadRequestException("Nodes without \"query\" cannot be returned as they do not be involved in match conditions");
        }
    }}

package it.larus.test.neotest.validator;

import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import it.larus.test.neotest.exception.BadRequestException;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

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
        if (queryV3Api.getNodeQueries().stream().anyMatch(nq -> isNull(nq.getLabel()))) {
            throw new BadRequestException("NodeQueries \"labels\" are mandatory");
        }
        if (queryV3Api.getRelQueries().stream().anyMatch(rq ->
                (isNull(rq.getLabel()) && rq.getMaxDepth() <= 0) ||
                (nonNull(rq.getLabel()) && rq.getMaxDepth() >= 0))
        ) {
            throw new BadRequestException("RelQueries \"label\" and \"maxDepth\" are mutually exclusive, populate one of them");
        }
    }
}

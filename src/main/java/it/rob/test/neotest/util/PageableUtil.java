package it.rob.test.neotest.util;

import it.rob.test.neotest.validator.ParamValidator;
import org.springframework.data.domain.PageRequest;

public class PageableUtil {

    private PageableUtil() {}

    public static PageRequest createPageRequest(int pageNumber, int numberOfResults) {
        ParamValidator.checkPageRequest(pageNumber, numberOfResults);
        return PageRequest.of(pageNumber, numberOfResults);
    }
}

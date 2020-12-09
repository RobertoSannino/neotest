package it.larus.test.neotest.util;

import it.larus.test.neotest.validator.ParamValidator;
import org.springframework.data.domain.PageRequest;

public class PageableUtil {

    private PageableUtil() {}

    public static PageRequest createPageRequest(int pageNumber, int numberOfResults) {
        ParamValidator.checkPageRequest(pageNumber, numberOfResults);
        return PageRequest.of(pageNumber, numberOfResults);
    }
}

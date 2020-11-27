package it.rob.test.neotest.util;

import org.springframework.data.domain.PageRequest;

public class PageableUtil {

    private PageableUtil() {}

    public static PageRequest createPageRequest(int pageNumber, int numberOfResults) {
        return PageRequest.of(pageNumber, numberOfResults);
    }
}

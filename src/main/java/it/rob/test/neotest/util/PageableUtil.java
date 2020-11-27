package it.rob.test.neotest.util;

import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;

public class PageableUtil {

    public static PageRequest createPageRequest(int pageNumber, int numberOfResults) {
        return PageRequest.of(pageNumber, numberOfResults);
    }
}

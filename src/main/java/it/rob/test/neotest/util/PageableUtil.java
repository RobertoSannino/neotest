package it.rob.test.neotest.util;

import it.rob.test.neotest.exception.BadRequestException;
import org.springframework.data.domain.PageRequest;

public class PageableUtil {

    private PageableUtil() {}

    public static PageRequest createPageRequest(int pageNumber, int numberOfResults) {
        if (pageNumber < 0 || numberOfResults < 0) {
            throw new BadRequestException("Page number and number of results must be greater than zero");
        }
        return PageRequest.of(pageNumber, numberOfResults);
    }
}

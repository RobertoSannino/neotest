package it.larus.test.neotest.validator;

import it.larus.test.neotest.exception.BadRequestException;

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

}

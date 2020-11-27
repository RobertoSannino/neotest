package it.rob.test.neotest.controller.neo4j;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.rob.test.neotest.elastic.entity.PfElastic;
import it.rob.test.neotest.ogm.entity.node.Pf;
import it.rob.test.neotest.service.PfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Size;

@RestController
public class PfController {

    private final PfService pfService;

    public PfController(PfService pfRepository) {
        this.pfService = pfRepository;
    }

    @Operation(summary = "Search for a Pf by codiceFiscale in Neo4j")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pf",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid number of characters, CF must be exactly 16 chars long",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @GetMapping(path = "/pf", produces = MediaType.APPLICATION_JSON_VALUE)
    public Pf getPf(@RequestParam(name = "cf") @Size(min = 16, max = 16) String codiceFiscale) {
        return pfService.getPfByCodiceFiscale(codiceFiscale);
    }
}

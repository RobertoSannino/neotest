package it.rob.test.neotest.controller.elastic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.rob.test.neotest.constant.SearchType;
import it.rob.test.neotest.elastic.entity.PfElastic;
import it.rob.test.neotest.ogm.queryresults.QRUfficiPf;
import it.rob.test.neotest.service.PfElasticService;
import it.rob.test.neotest.service.PfService;
import it.rob.test.neotest.util.PageableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;

@Slf4j
@RestController
public class PfElasticController {

    private final PfElasticService elasticService;
    private final PfService pfService;

    public PfElasticController(PfElasticService elasticService, PfService pfService) {
        this.elasticService = elasticService;
        this.pfService = pfService;
    }

    @Operation(summary = "Search for Pfs by name and search type in Elastic, results are paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @GetMapping(path = "/pf-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PfElastic> getPfByName(@RequestParam(name = "name") String name,
                                @RequestParam(name = "searchType") @Parameter(description = "EXACT; CONTAINS; STARTS_WITH") SearchType searchType,
                                @RequestParam(name = "page") @Positive int pageNumber,
                                @RequestParam(name = "results") @Positive int numberOfResults) {
        return elasticService.getPfByDenominazione(name, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults));
    }

    @Operation(summary = "Search for Pfs by name and search type in Elastic and then search for paths between them and UfficiTerritoriali in Neo4j, results are paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @GetMapping(path = "/uffici-by-pf-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getUfficiByPfByName(@RequestParam(name = "name") String name,
                                         @RequestParam(name = "searchType")  @Parameter(description = "EXACT; CONTAINS; STARTS_WITH") SearchType searchType,
                                         @RequestParam(name = "page") @Positive int pageNumber,
                                         @RequestParam(name = "results") @Positive int numberOfResults) {

        return pfService.getUfficitTerritorialiByPfDenoms(
                emptyIfNull(elasticService.getPfByDenominazione(name, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults)))
                        .stream().map(PfElastic::getCodiceFiscale).collect(Collectors.toList()));
    }
}

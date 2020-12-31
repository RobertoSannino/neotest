package it.larus.test.neotest.controller.elastic;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import it.larus.test.neotest.api.v1.QueryApi;
import it.larus.test.neotest.api.v2.QueryV2Api;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import it.larus.test.neotest.constant.SearchType;
import it.larus.test.neotest.elastic.entity.PfElastic;
import it.larus.test.neotest.ogm.queryresults.QRUfficiPf;
import it.larus.test.neotest.service.PfElasticService;
import it.larus.test.neotest.service.PfService;
import it.larus.test.neotest.util.PageableUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Positive;
import java.util.Collection;
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
    @GetMapping(path = "/elastic/pf-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
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
    @GetMapping(path = "/elastic/uffici-by-pf-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getUfficiByPfByName(@RequestParam(name = "name") String name,
                                                @RequestParam(name = "searchType")  @Parameter(description = "EXACT; CONTAINS; STARTS_WITH") SearchType searchType,
                                                @RequestParam(name = "page") @Positive int pageNumber,
                                                @RequestParam(name = "results") @Positive int numberOfResults) {

        return pfService.getUfficitTerritorialiByPfDenoms(
                emptyIfNull(elasticService.getPfByDenominazione(name, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults)))
                        .stream().map(PfElastic::getCodiceFiscale).collect(Collectors.toList()));
    }

    @Operation(summary = "Search for Pfs by cf and search type in Elastic and then search for paths between them and UfficiTerritoriali in Neo4j, results are paginated")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @GetMapping(path = "/elastic/uffici-by-pf-cf", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getUfficiByPfCf(@RequestParam(name = "cf") String cf,
                                                @RequestParam(name = "searchType")  @Parameter(description = "EXACT; CONTAINS; STARTS_WITH") SearchType searchType,
                                                @RequestParam(name = "page") @Positive int pageNumber,
                                                @RequestParam(name = "results") @Positive int numberOfResults) {

        return pfService.getUfficitTerritorialiByPfDenoms(
                emptyIfNull(elasticService.getPfByCf(cf, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults)))
                        .stream().map(PfElastic::getCodiceFiscale).collect(Collectors.toList()));
    }

    @Operation(summary = "Search for Pfs by denom and search type in Elastic and then search for paths between them and UfficiTerritoriali in Neo4j, results are paginated" +
            "and aggregated in terms of query logic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @PostMapping(path = "/elastic/uffici-by-queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getUfficiByPfQueries(@RequestBody List<QueryApi> queriesApi,
                                                 @RequestParam(name = "queryLogic") String queryLogic,
                                                 @RequestParam(name = "limit") @Positive int numberOfResults) {
         return pfService.getUfficitTerritorialiByQueries(
                 queriesApi.stream().map(q -> elasticService.getPfByDenominazione(q.getValue(), q.getSearchType(), PageableUtil.createPageRequest(0, numberOfResults)))
                .flatMap(Collection::stream).map(PfElastic::getCodiceFiscale).collect(Collectors.toList()),
                 queryLogic
         );
    }

    @Operation(summary = "Search for entities in Elastic by queries , results are paginated" +
            "and aggregated in terms of query logic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @PostMapping(path = "/elastic/v2/uffici-by-queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getEntitiesByQueries(@RequestBody QueryV2Api queryV2Api,
                                                 /*@RequestParam(name = "queryLogic") String queryLogic,*/
                                                 @RequestParam(name = "limit") @Positive int numberOfResults) {

        elasticService.resolveQuery(queryV2Api);
        return null;
    }

    @Operation(summary = "Search for entities in Elastic by ES queries, results are paginated" +
            "and aggregated in terms of query logic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @PostMapping(path = "/elastic/v2/uffici-by-es-queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntitiesByESQueries(@RequestBody QueryV3Api queryV3Api,
                                         @RequestParam(name = "limit_nodes") @Positive int limitNode,
                                         @RequestParam(name = "limit_rel") @Positive int limitRel
    ) {
        return elasticService.resolveQuery(queryV3Api, limitNode, limitRel).toString();
    }

    @Operation(summary = "Search for entities in Elastic by ES queries, results are paginated" +
            "and aggregated in terms of query logic")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found Pfs and paths",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PfElastic.class)) }),
            @ApiResponse(responseCode = "400", description = "Invalid searchType or pagination supplied",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "No results for the research",
                    content = @Content) })
    @PostMapping(path = "/elastic/v2/uffici-by-es-expand-queries", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getEntitiesByESExpandQueries(@RequestBody List<QueryV3Api> queryV3Apis,
                                         @RequestParam(name = "limit_nodes") @Positive int limitNode,
                                         @RequestParam(name = "limit_rel") @Positive int limitRel
    ) {
        return elasticService.resolveExpandQuery(queryV3Apis, limitNode, limitRel).toString();
    }
}

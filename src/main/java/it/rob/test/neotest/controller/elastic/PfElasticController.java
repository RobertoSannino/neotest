package it.rob.test.neotest.controller.elastic;

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

    @GetMapping(path = "/pf-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<PfElastic> getPfByName(@RequestParam(name = "name") String name,
                                @RequestParam(name = "searchType") SearchType searchType,
                                @RequestParam(name = "page") int pageNumber,
                                @RequestParam(name = "results") int numberOfResults) {
        return elasticService.getPfByDenominazione(name, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults));
    }

    @GetMapping(path = "/uffici-by-pf-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRUfficiPf> getUfficiByPfByName(@RequestParam(name = "name") String name,
                                         @RequestParam(name = "searchType") SearchType searchType,
                                         @RequestParam(name = "page") int pageNumber,
                                         @RequestParam(name = "results") int numberOfResults) {

        return pfService.getUfficitTerritorialiByPfDenoms(
                emptyIfNull(elasticService.getPfByDenominazione(name, searchType, PageableUtil.createPageRequest(pageNumber, numberOfResults)))
                        .stream().map(PfElastic::getCodiceFiscale).collect(Collectors.toList()));
    }
}

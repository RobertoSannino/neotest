package it.rob.test.neotest.controller.elastic;

import it.rob.test.neotest.elastic.PfElasticRepository;
import it.rob.test.neotest.elastic.entity.PfElastic;
import it.rob.test.neotest.ogm.queryresults.QRUfficiPf;
import it.rob.test.neotest.ogm.repository.PfRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
public class ElasticController {

    private final PfElasticRepository elasticRepository;
    private final PfRepository pfRepository;

    public ElasticController(PfElasticRepository elasticRepository, PfRepository pfRepository) {
        this.elasticRepository = elasticRepository;
        this.pfRepository = pfRepository;
    }

   /* @GetMapping(path = "/article-page", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Article> getArticlesPage(@RequestParam(name = "page") int pageNumber, @RequestParam(name = "results") int numberOfResults) {
        Pageable pageRequest = PageRequest.of(pageNumber, numberOfResults);
        return IterableUtils.toList(elasticRepository.findAll(pageRequest));
    }*/

    @GetMapping(path = "/pf-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    List<PfElastic> getPfByName(@RequestParam(name = "name") String name, @RequestParam(name = "page") int pageNumber, @RequestParam(name = "results") int numberOfResults) {
        Pageable pageRequest = PageRequest.of(pageNumber, numberOfResults);
        return IterableUtils.toList(elasticRepository.findByDenominazione(name, pageRequest));
    }

    @GetMapping(path = "/uffici-by-pf-name", produces = MediaType.APPLICATION_JSON_VALUE)
    List<QRUfficiPf> getUfficiByPfByName(@RequestParam(name = "name") String name,
                                         @RequestParam(name = "searchType") String searchType,
                                         @RequestParam(name = "page") int pageNumber, @RequestParam(name = "results") int numberOfResults) {
        Pageable pageRequest = PageRequest.of(pageNumber, numberOfResults);
        log.warn("=== INIZIO CHIAMATA ELASTIC ===");

        List<PfElastic> pfElastics = new ArrayList<>();
        switch (searchType) {
            case "EXACT":
                pfElastics = IterableUtils.toList(elasticRepository.findByDenominazione(name, pageRequest));
                break;
            case "STARTS WITH":
                pfElastics = IterableUtils.toList(elasticRepository.findByDenominazioneStartingWith(name, pageRequest));
                break;
            case "CONTAINS":
                pfElastics = IterableUtils.toList(elasticRepository.findByDenominazioneContaining(name, pageRequest));
                break;
            default:
                break;
        }

        List<String> cfs = pfElastics.stream().map(PfElastic::getCodiceFiscale).collect(Collectors.toList());
        log.warn("=== #Codici Fiscali elastic: {}", cfs.size());

        List<QRUfficiPf> ufficiByPfNames = pfRepository.findUfficiByPfNames(cfs);
        log.warn("=== #Path Neo4j trovati: {}", ufficiByPfNames.size());

        return ufficiByPfNames;
    }
}

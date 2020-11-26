package it.rob.test.neotest.controller.elastic;

import it.rob.test.neotest.elastic.PfElasticRepository;
import it.rob.test.neotest.elastic.entity.Article;
import it.rob.test.neotest.elastic.entity.PfElastic;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ElasticController {

    private final PfElasticRepository elasticRepository;

    public ElasticController(PfElasticRepository elasticRepository) { this.elasticRepository = elasticRepository; }

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
}

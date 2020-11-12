package it.rob.test.neotest.controller.elastic;

import it.rob.test.neotest.elastic.ArticleRepository;
import it.rob.test.neotest.elastic.entity.Article;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ArticleController {

    private final ArticleRepository articleRepository;

    public ArticleController (ArticleRepository articleRepository) { this.articleRepository = articleRepository; }

    @GetMapping(path = "/article-page", produces = MediaType.APPLICATION_JSON_VALUE)
    List<Article> getArticlesPage(@RequestParam(name = "page") int pageNumber, @RequestParam(name = "results") int numberOfResults) {
        Pageable pageRequest = PageRequest.of(pageNumber, numberOfResults);
        return IterableUtils.toList(articleRepository.findAll(pageRequest));
    }
}

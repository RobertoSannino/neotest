package it.rob.test.neotest.elastic;

import it.rob.test.neotest.elastic.entity.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.domain.Pageable;

public interface ArticleRepository extends ElasticsearchRepository<Article, String> {

    Page<Article> findByTitle(String title, Pageable pageable);

    @Query("{\"match_all\": {}}")
    Page<Article> findAll(Pageable pageable);
}
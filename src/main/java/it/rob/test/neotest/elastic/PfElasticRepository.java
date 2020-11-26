package it.rob.test.neotest.elastic;

import it.rob.test.neotest.elastic.entity.PfElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;

public interface PfElasticRepository extends ElasticsearchRepository<PfElastic, String> {

    Page<PfElastic> findByDenominazione(String denominazione, Pageable pageable);

    @Query("{\"match_all\": {}}")
    Page<PfElastic> findAll(Pageable pageable);
}
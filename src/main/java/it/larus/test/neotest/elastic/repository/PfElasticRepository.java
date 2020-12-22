package it.larus.test.neotest.elastic.repository;

import it.larus.test.neotest.elastic.entity.PfElastic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface PfElasticRepository extends ElasticsearchRepository<PfElastic, String> {

    Page<PfElastic> findByDenominazione(String denominazione, Pageable pageable);

    Page<PfElastic> findByDenominazioneStartingWith(String denominazione, Pageable pageable);

    Page<PfElastic> findByDenominazioneContaining(String denominazione, Pageable pageable);

    Page<PfElastic> findByCodiceFiscale(String denominazione, Pageable pageable);

    Page<PfElastic> findByCodiceFiscaleStartingWith(String denominazione, Pageable pageable);

    Page<PfElastic> findByCodiceFiscaleContaining(String denominazione, Pageable pageable);

    @Query("{\"match_all\": {}}")
    Page<PfElastic> findAll(Pageable pageable);
}
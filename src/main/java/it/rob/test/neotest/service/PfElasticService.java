package it.rob.test.neotest.service;

import it.rob.test.neotest.api.v2.NodeQuery;
import it.rob.test.neotest.api.v2.QueryV2Api;
import it.rob.test.neotest.api.v2.RelQuery;
import it.rob.test.neotest.constant.SearchType;
import it.rob.test.neotest.elastic.entity.PfElastic;
import it.rob.test.neotest.elastic.repository.GenericElasticRepository;
import it.rob.test.neotest.elastic.repository.PfElasticRepository;
import it.rob.test.neotest.exception.BadRequestException;
import it.rob.test.neotest.exception.NotFoundException;
import it.rob.test.neotest.ogm.repository.GenericNeo4jRepository;
import it.rob.test.neotest.util.ElasticUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static org.apache.commons.collections4.ListUtils.emptyIfNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
public class PfElasticService {

    private final PfElasticRepository pfElasticRepository;

    public PfElasticService(PfElasticRepository pfRepository) {
        this.pfElasticRepository = pfRepository;
    }

    public List<PfElastic> getPfByDenominazione(String name, SearchType searchType, Pageable pageRequest) {
        return getPfBySearchType(name,
                searchType,
                pageRequest,
                pfElasticRepository::findByDenominazione,
                pfElasticRepository::findByDenominazioneContaining,
                pfElasticRepository::findByDenominazioneStartingWith);
    }

    public List<PfElastic> getPfByCf(String cf, SearchType searchType, Pageable pageRequest) {
        return getPfBySearchType(cf,
                searchType,
                pageRequest,
                pfElasticRepository::findByCodiceFiscale,
                pfElasticRepository::findByCodiceFiscaleContaining,
                pfElasticRepository::findByCodiceFiscaleStartingWith);
    }

    public void fillXonarIds(QueryV2Api queryV2Api) {
        GenericElasticRepository ger = new GenericElasticRepository();
        queryV2Api.getNodeQueries().forEach(nq -> nq.setIdsXonarRequired(!isEmpty(nq.getConstraints())));

        for (NodeQuery nq : queryV2Api.getNodeQueries()) {
            String indexName = ElasticUtils.getIndexForLabel(nq.getLabel());
            List<String> xonarIds = ger.getIdsFor(indexName, nq.getLabel(), emptyIfNull(nq.getConstraints()));
            log.info("IDs for {}-{}: {}", nq.getId(), nq.getLabel(), xonarIds);
            nq.setIdsXonar(xonarIds);
        }

        GenericNeo4jRepository gnr = new GenericNeo4jRepository();
        String query = "";
        // List<Map<String, Object>> paths = new ArrayList<>();

        int i = 0;
        for (RelQuery rq : queryV2Api.getRelQueries()) {
            query = query + " \n" + (gnr.generateMatchPath(
                    i,
                    queryV2Api.getNodeQueries().stream().filter(nq -> nq.getId() == rq.getStart()).findFirst().get(),
                    queryV2Api.getNodeQueries().stream().filter(nq -> nq.getId() == rq.getEnd()).findFirst().get(),
                    rq.getLabel()
            ));
            i++;
        }

        query = query + " \nRETURN *";
        log.info("Query created: {}", query);
        List<Map<String, Object>> paths = gnr.runCypherQuery(query);

        log.info("Path results: {}", paths);

    }

    private List<PfElastic> getPfBySearchType(String value, SearchType searchType, Pageable pageRequest,
                                              BiFunction<String, Pageable, Page<PfElastic>> exact,
                                              BiFunction<String, Pageable, Page<PfElastic>> contains,
                                              BiFunction<String, Pageable, Page<PfElastic>> startsWith) {
        List<PfElastic> pfElastics;
        switch (searchType) {
            case EXACT:
                pfElastics = IterableUtils.toList(exact.apply(value, pageRequest));
                break;
            case CONTAINS:
                pfElastics = IterableUtils.toList(contains.apply(value, pageRequest));
                break;
            case STARTS_WITH:
                pfElastics = IterableUtils.toList(startsWith.apply(value, pageRequest));
                break;
            default:
                throw new BadRequestException(MessageFormat.format("SearchType must be one of the following: {0}", EnumSet.allOf(SearchType.class)));
        }

        if(isEmpty(pfElastics)) {
            throw new NotFoundException(MessageFormat.format(
                    "Cannot find Pf with {0} using SEARCH_TYPE {1}, in PAGE {2} with PAGE_SIZE {3}",
                    value, searchType, pageRequest.getPageNumber(), pageRequest.getPageSize())
            );
        }

        return pfElastics;
    }


}

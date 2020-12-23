package it.larus.test.neotest.service;

import it.larus.test.neotest.api.v2.RelQuery;
import it.larus.test.neotest.api.v3.v2.NodeQueryV3Api;
import it.larus.test.neotest.api.v3.v2.QueryV3Api;
import it.larus.test.neotest.constant.SearchType;
import it.larus.test.neotest.elastic.entity.PfElastic;
import it.larus.test.neotest.elastic.repository.GenericElasticRepository;
import it.larus.test.neotest.elastic.repository.PfElasticRepository;
import it.larus.test.neotest.ogm.repository.GenericNeo4jRepository;
import it.larus.test.neotest.util.ElasticUtils;
import it.larus.test.neotest.api.v2.NodeQuery;
import it.larus.test.neotest.api.v2.QueryV2Api;
import it.larus.test.neotest.exception.BadRequestException;
import it.larus.test.neotest.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.Positive;
import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

import static java.util.Objects.nonNull;
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

    public void resolveQuery(QueryV2Api queryV2Api) {
        GenericElasticRepository ger = new GenericElasticRepository();
        queryV2Api.getNodeQueries().forEach(nq -> nq.setIdsXonarRequired(!isEmpty(nq.getConstraints())));

        for (NodeQuery nodeQuery : queryV2Api.getNodeQueries()) {
            String indexName = ElasticUtils.getIndexForLabel(nodeQuery.getLabel());
            List<String> xonarIds = ger.getIdsFor(indexName, nodeQuery.getLabel(), emptyIfNull(nodeQuery.getConstraints()));
            log.info("IDs for {}-{}: {}", nodeQuery.getId(), nodeQuery.getLabel(), xonarIds);
            nodeQuery.setIdsXonar(xonarIds);
        }

        GenericNeo4jRepository gnr = new GenericNeo4jRepository();
        StringBuilder query = new StringBuilder();

        int i = 0;
        for (RelQuery relationshipQuery : queryV2Api.getRelQueries()) {
            query.append(" \n").append(gnr.generateMatchPath(
                    i,
                    queryV2Api.getNodeQueries().stream().filter(nq -> nq.getId() == relationshipQuery.getStart()).findFirst().get(),
                    queryV2Api.getNodeQueries().stream().filter(nq -> nq.getId() == relationshipQuery.getEnd()).findFirst().get(),
                    relationshipQuery.getLabel()
            ));
            i++;
        }

        query.append(" \nRETURN *");
        log.info("Query created: {}", query.toString());
        List<Map<String, Object>> paths = gnr.runCypherQuery(query.toString());

        log.info("Path results: {}", paths);
    }

    public void resolveQuery(QueryV3Api queryV3Api, @Positive int limitNode, @Positive int limitRel) {
        GenericElasticRepository ger = new GenericElasticRepository();
        queryV3Api.getNodeQueries().forEach(nq -> nq.setIdsXonarRequired(true));

        for (NodeQueryV3Api nodeQuery : queryV3Api.getNodeQueries()) {
            String indexName = ElasticUtils.getIndexForLabel(nodeQuery.getLabel());
            List<String> xonarIds = ger.getIdsFor(indexName, nodeQuery.getLabel(), nodeQuery.getQuery(), limitNode);
            log.info("IDs for {}-{}: {}", nodeQuery.getId(), nodeQuery.getLabel(), xonarIds);
            nodeQuery.setIdsXonar(xonarIds);
        }

        GenericNeo4jRepository gnr = new GenericNeo4jRepository();
        StringBuilder query = new StringBuilder();

        int i = 0;
        for (RelQuery relationshipQuery : queryV3Api.getRelQueries()) {
            NodeQuery startNode = queryV3Api.getNodeQueries().stream().filter(nq -> nq.getId() == relationshipQuery.getStart()).findFirst().get();
            NodeQuery endNode = queryV3Api.getNodeQueries().stream().filter(nq -> nq.getId() == relationshipQuery.getEnd()).findFirst().get();
            query.append(" \n").append(gnr.generateMatchPath(i, startNode, endNode, relationshipQuery.getLabel()));
            i++;
        }

        query.append(" \nRETURN * ");

        if (nonNull(queryV3Api.getOrderByNodes())) {
            query.append(" \nORDER BY " );
            queryV3Api.getOrderByNodes().forEach(o -> query.append(gnr.generateOrderBy(o) + ", "));
            query.deleteCharAt(query.lastIndexOf(", "));
        }

        query.append("\nLIMIT " + limitRel);

        log.info("Query created: {}", query.toString());
        List<Map<String, Object>> paths = gnr.runCypherQuery(query.toString());

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

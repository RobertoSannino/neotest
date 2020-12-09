package it.larus.test.neotest.service;

import it.larus.test.neotest.elastic.repository.GenericElasticRepository;
import it.larus.test.neotest.exception.BadRequestException;
import it.larus.test.neotest.exception.NotFoundException;
import it.larus.test.neotest.constant.SearchType;
import it.larus.test.neotest.elastic.entity.PfElastic;
import it.larus.test.neotest.elastic.repository.PfElasticRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
public class PfElasticService {

    private final PfElasticRepository pfElasticRepository;

    @Autowired
    private GenericElasticRepository elasticRepository;

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

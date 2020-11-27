package it.rob.test.neotest.service;

import it.rob.test.neotest.constant.SearchType;
import it.rob.test.neotest.elastic.entity.PfElastic;
import it.rob.test.neotest.elastic.repository.PfElasticRepository;
import it.rob.test.neotest.exception.BadRequestException;
import it.rob.test.neotest.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
public class PfElasticService {

    private final PfElasticRepository pfElasticRepository;

    public PfElasticService(PfElasticRepository pfRepository) {
        this.pfElasticRepository = pfRepository;
    }

    public List<PfElastic> getPfByDenominazione(String name, SearchType searchType, Pageable pageRequest) {
        List<PfElastic> pfElastics;
        switch (searchType) {
            case EXACT:
                pfElastics = IterableUtils.toList(pfElasticRepository.findByDenominazione(name, pageRequest));
                break;
            case CONTAINS:
                pfElastics = IterableUtils.toList(pfElasticRepository.findByDenominazioneContaining(name, pageRequest));
                break;
            case STARTS_WITH:
                pfElastics = IterableUtils.toList(pfElasticRepository.findByDenominazioneStartingWith(name, pageRequest));
                break;
            default:
                throw new BadRequestException("");
        }

        if(isEmpty(pfElastics)) {
            throw new NotFoundException(MessageFormat.format(
                    "Cannot find Pf with DENOM {0} using SEARCH_TYPE {1}, in PAGE {2} with PAGE_SIZE {3}",
                    name, searchType, pageRequest.getPageNumber(), pageRequest.getPageSize())
            );
        }

        return pfElastics;
    }
}

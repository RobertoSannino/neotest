package it.larus.test.neotest.service;

import it.larus.test.neotest.ogm.entity.node.Pf;
import it.larus.test.neotest.ogm.queryresults.QRUfficiPf;
import it.larus.test.neotest.ogm.repository.PfRepository;
import it.larus.test.neotest.api.v1.QueryApi;
import it.larus.test.neotest.exception.NotFoundException;
import it.larus.test.neotest.validator.ParamValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

import static java.util.Objects.isNull;
import static org.apache.commons.collections4.CollectionUtils.isEmpty;

@Slf4j
@Service
public class PfService {

    private final PfRepository pfRepository;

    public PfService(PfRepository personRepository) {
        this.pfRepository = personRepository;
    }

    public Pf getPfByCodiceFiscale(String codiceFiscale) {
        ParamValidator.checkCodiceFiscale(codiceFiscale);

        Pf pfByCodiceFiscale = pfRepository.getPfByCodiceFiscale(codiceFiscale);
        if (isNull(pfByCodiceFiscale)){
            throw new NotFoundException(MessageFormat.format("Cannot find Pf with CODICE_FISCALE {0}", codiceFiscale));
        }
        return pfByCodiceFiscale;
    }

    public List<QRUfficiPf> getUfficitTerritorialiByPfDenoms(List<String> pfDenoms) {

        List<QRUfficiPf> ufficiByPfNames = pfRepository.findUfficiByPfNames(pfDenoms);
        if(isEmpty(ufficiByPfNames)) {
            throw new NotFoundException("Cannot find UfficiTerritoriali linked to the specified Pfs");
        }

        return ufficiByPfNames;
    }

    public List<QRUfficiPf> getUfficitTerritorialiByQueries(List<String> cfList, String queryLogic) {

        log.warn("cflist: {}", cfList);
        List<QRUfficiPf> ufficiByPfNames = pfRepository.findUfficiByQueries(cfList, queryLogic);

        if(isEmpty(ufficiByPfNames) || ("and".equalsIgnoreCase(queryLogic) && ufficiByPfNames.size() != cfList.size())) {
            throw new NotFoundException("Cannot find UfficiTerritoriali linked to the specified Pfs");
        }

        return ufficiByPfNames;
    }

    public List<QRUfficiPf> getUfficitTerritorialiByQueriesFromNeo(List<QueryApi> cfList, String queryLogic, int limit) {

        List<QRUfficiPf> ufficiByPfNames = pfRepository.findUfficiByQueriesNeo(cfList, queryLogic, limit);

        if(isEmpty(ufficiByPfNames) || ("and".equalsIgnoreCase(queryLogic) && ufficiByPfNames.size() != cfList.size())) {
            throw new NotFoundException("Cannot find UfficiTerritoriali linked to the specified Pfs");
        }

        return ufficiByPfNames;
    }




}

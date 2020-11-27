package it.rob.test.neotest.service;

import it.rob.test.neotest.exception.BadRequestException;
import it.rob.test.neotest.exception.NotFoundException;
import it.rob.test.neotest.ogm.entity.node.Pf;
import it.rob.test.neotest.ogm.queryresults.QRUfficiPf;
import it.rob.test.neotest.ogm.repository.PfRepository;
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
        if (codiceFiscale.length() != 16) {
            throw new BadRequestException("CF must be exactly 16 chars long");
        }

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


}

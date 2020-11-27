package it.rob.test.neotest.controller.neo4j;

import it.rob.test.neotest.ogm.entity.node.Pf;
import it.rob.test.neotest.service.PfService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PfController {

    private final PfService pfService;

    public PfController(PfService pfRepository) {
        this.pfService = pfRepository;
    }

    @GetMapping(path = "/pf", produces = MediaType.APPLICATION_JSON_VALUE)
    public Pf getPf(@RequestParam(name = "cf") String codiceFiscale) {
        return pfService.getPfByCodiceFiscale(codiceFiscale);
    }
}

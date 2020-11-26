package it.rob.test.neotest.controller.neo4j;

import it.rob.test.neotest.ogm.entity.node.Pf;
import it.rob.test.neotest.ogm.repository.PfRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class PfController {

    private final PfRepository pfRepository;

    public PfController(PfRepository pfRepository) {
        this.pfRepository = pfRepository;
    }

    @GetMapping(path = "/pf", produces = MediaType.APPLICATION_JSON_VALUE)
    public Pf getPf(@RequestParam(name = "cf") String codiceFiscale) {
        return pfRepository.getPfByCodiceFiscale(codiceFiscale);
    }
/*
    @GetMapping(path = "/person-by-name", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> getPersonByNameInList(@RequestParam(name = "names") List<String> namesList) {
        return personService.getPersonByNameInList(namesList);
    }

    @GetMapping(path = "/person/actAndDirectMovie", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<QRPersonActedInAndDirectedMovie> getPersonActAndDirectMovie() {
        return personService.getPersonWhoActedAndDirected();
    }

    @GetMapping(path = "/person/{something}", produces = MediaType.APPLICATION_JSON_VALUE)
    public String testPathVariable(@PathVariable(value = "something") String something) {
        return something;
    }

 */
}

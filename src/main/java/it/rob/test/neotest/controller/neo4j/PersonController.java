package it.rob.test.neotest.controller.neo4j;

import it.rob.test.neotest.ogm.entity.node.Person;
import it.rob.test.neotest.ogm.queryresults.QRPersonActedInAndDirectedMovie;
import it.rob.test.neotest.service.PersonService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class PersonController {

    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @GetMapping(path = "/person", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Person> getPerson(@RequestParam(name = "name") Optional<String> name) {
        return personService.getPerson(name);
    }

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
}

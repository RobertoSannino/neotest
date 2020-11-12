package it.rob.test.neotest.service;

import it.rob.test.neotest.ogm.entity.node.Person;
import it.rob.test.neotest.ogm.queryresults.QRPersonActedInAndDirectedMovie;
import it.rob.test.neotest.ogm.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.IterableUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public List<Person> getPerson(Optional<String> name) {
        return name.map(s -> IterableUtils.toList(personRepository.findPersonByNameLike(s)))
                .orElseGet(() -> IterableUtils.toList(personRepository.findAll()));
    }

    public List<QRPersonActedInAndDirectedMovie> getPersonWhoActedAndDirected() {
        return personRepository.getPersonsWhoActAndDirectAMovieQR();
    }

    public List<Person> getPersonByNameInList(List<String> namesList) {
        return personRepository.findByNameInList(namesList);
    }
}

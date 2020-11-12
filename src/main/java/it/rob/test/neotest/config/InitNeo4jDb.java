package it.rob.test.neotest.config;

import it.rob.test.neotest.ogm.entity.node.Person;
import it.rob.test.neotest.ogm.repository.PersonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class InitNeo4jDb {
/*
    @Bean
    CommandLineRunner initDatabase(PersonRepository personRepository) {

        return args -> {
            log.info("Preloading " + personRepository.save(new Person("Ciccio Gamer")));
        };
    }

 */
}

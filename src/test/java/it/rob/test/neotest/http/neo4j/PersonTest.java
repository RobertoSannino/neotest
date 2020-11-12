package it.rob.test.neotest.http.neo4j;

import it.rob.test.neotest.ogm.entity.node.Person;
import it.rob.test.neotest.ogm.queryresults.QRPersonActedInAndDirectedMovie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getTomHanks() throws Exception {
        String local = "http://localhost:" + port;

        Person tomHanks = this.restTemplate.getForObject(local + "/person?name=Tom Hanks", Person[].class)[0];
        assertEquals(1956, tomHanks.getBirthyear());
    }

    @Test
    public void personNotFound() throws Exception {
        String local = "http://localhost:" + port;

        Person[] personList = this.restTemplate.getForObject(local + "/person?name=Francesco Totti", Person[].class);
        assertEquals(0, personList.length);
    }

    @Test
    public void getPersonByNameInList() throws Exception {
        String local = "http://localhost:" + port;

        Person[] personList = this.restTemplate.getForObject(local + "/person-by-name?names=Tom Hanks,Francesco Totti", Person[].class);
        assertEquals(1, personList.length);
        assertEquals(1956, personList[0].getBirthyear());
    }

    @Test
    public void getPersonWhoActedAndDirectedAMovie() throws Exception {
        String local = "http://localhost:" + port;

        QRPersonActedInAndDirectedMovie[] personList = this.restTemplate.getForObject(local + "/person/actAndDirectMovie", QRPersonActedInAndDirectedMovie[].class);
        List<QRPersonActedInAndDirectedMovie> res = Arrays.asList(personList);
        assertEquals(3, res.size());
        assertTrue(res.stream().map(r -> r.getPerson()).anyMatch(p -> p.getName().equals("Tom Hanks")));
        assertTrue(res.stream().filter(r -> r.getPerson().getName().equals("Tom Hanks")).anyMatch(r -> r.getMovie().getTitle().equals("That Thing You Do")));
    }

}

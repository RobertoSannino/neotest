package it.rob.test.neotest.http.neo4j;

import it.rob.test.neotest.ogm.entity.node.Pf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import static java.util.Objects.isNull;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PfTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getByCf() throws Exception {
        String local = "http://localhost:" + port;

        Pf pf = this.restTemplate.getForObject(local + "/pf?cf=CDRLCU58M49M132G", Pf.class);
        assertEquals("CADEA19E5EEBCDCF577BA52A42EE39C7", pf.getZzCode());
    }

    @Test
    public void pfNotFound() throws Exception {
        String local = "http://localhost:" + port;

        Pf pf = this.restTemplate.getForObject(local + "/pf?cf=CDR", Pf.class);
        assertTrue(isNull(pf) || isNull(pf.getId()));
    }

}

package it.rob.test.neotest.http.elastic;

import it.rob.test.neotest.ogm.queryresults.QRUfficiPf;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UfficiTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void getUfficiByPfName() throws Exception {
        String local = "http://localhost:" + port;

        QRUfficiPf[] ufficiPfs = this.restTemplate.getForObject(local + "uffici-by-pf-name?name=MARCO&searchType=CONTAINS&page=0&results=10", QRUfficiPf[].class);
        List<QRUfficiPf> uffici = Arrays.asList(ufficiPfs);

        assertFalse(uffici.isEmpty());
    }

}

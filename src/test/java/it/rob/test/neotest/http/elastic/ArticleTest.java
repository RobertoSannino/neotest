package it.rob.test.neotest.http.elastic;

import it.rob.test.neotest.elastic.entity.Article;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ArticleTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void matchAll() throws Exception {
        String local = "http://localhost:" + port;

        Article[] articlesArray = this.restTemplate.getForObject(local + "/article-page?page=0&results=5", Article[].class);
        List<Article> articles = Arrays.asList(articlesArray);

        assertFalse(articles.isEmpty());
        assertTrue(articles.get(0).getId().length() > 0);
        assertTrue(articles.get(0).getTitle().length() > 0);
        assertTrue(articles.get(0).getRandomText().length() > 0);
    }

}

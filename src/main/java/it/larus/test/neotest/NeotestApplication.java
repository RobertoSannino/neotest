package it.larus.test.neotest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@EnableNeo4jRepositories
public class NeotestApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeotestApplication.class, args);
	}

}

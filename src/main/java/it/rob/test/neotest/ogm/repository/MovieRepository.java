package it.rob.test.neotest.ogm.repository;

import it.rob.test.neotest.ogm.entity.node.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MovieRepository extends Neo4jRepository<Movie, Long> {

    Movie getMovieByTitle(String title);

    Iterable<Movie> findMovieByTitleLike(String title);
}
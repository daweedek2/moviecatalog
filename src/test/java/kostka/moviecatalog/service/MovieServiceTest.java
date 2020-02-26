package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class MovieServiceTest {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieService movieService;

    @Test
    public void createMovieTest() {
        movieService.createMovie("test");

        Assertions.assertEquals(1, movieRepository.findAll().size());
        Assertions.assertTrue(movieRepository.count() == 1);
    }
}

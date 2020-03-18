package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class MovieServiceImplTest {
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private MovieServiceImpl movieServiceImpl;

    @Test
    public void createMovieTest() {
        movieServiceImpl.createMovie("test");

        Assertions.assertEquals(1, movieRepository.findAll().size());
        Assertions.assertTrue(movieRepository.count() == 1);
    }

    @Test void getAllMoviesTest() {
        List<Movie> allMovies = movieServiceImpl.getAllMovies();

        Assertions.assertEquals(8, allMovies.size());
    }
}

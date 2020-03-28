package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieDto;
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
public class DbMovieServiceTest {
    public static final String TEST_NAME = "testName";
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DbMovieService dbMovieService;

    @Test
    public void createMovieTest() {
        MovieDto dto = new MovieDto();
        dto.setName(TEST_NAME);
        dbMovieService.createMovie(dto);


        Assertions.assertEquals(1, movieRepository.findAll().size());
        Assertions.assertEquals(1, movieRepository.count());
    }

    @Test void getAllMoviesTest() {
        List<Movie> allMovies = dbMovieService.getAllMoviesFromDB();

        Assertions.assertEquals(8, allMovies.size());
    }
}

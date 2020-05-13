package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class DbMovieServiceIntegrationTest {
    public static final String TEST_NAME = "testName";
    public static final String TEST_NAME_2 = "testName2";
    public static final String TEST_CAMERA = "testCamera";
    public static final String TEST_DESCRIPTION = "testDescription";
    public static final String TEST_DIRECTOR = "testDirector";
    public static final String TEST_MUSIC = "testMusic";
    public static final long NON_EXISTING_MOVIE_ID = 0L;

    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private DbMovieService dbMovieService;
    @MockBean
    EsMovieService esMovieService;
    @MockBean
    MovieElasticSearchRepository movieElasticSearchRepository;

    @Before
    public void createTestMovie() {
        MovieFormDto dto = new MovieFormDto();
        dto.setName(TEST_NAME);
        dto.setCamera(TEST_CAMERA);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setDirector(TEST_DIRECTOR);
        dto.setMusic(TEST_MUSIC);
        dbMovieService.createMovie(dto);
    }

    @Test
    public void createMovieIntegrationTest() {
        long countBefore = movieRepository.count();
        MovieFormDto dto = new MovieFormDto();
        dto.setName(TEST_NAME_2);
        dbMovieService.createMovie(dto);

        Assert.assertEquals(countBefore + 1, movieRepository.count());
        assertThat(movieRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    public void doNotCreateMovieWithEmptyDtoIntegrationTest() {
        long countBefore = movieRepository.count();
        MovieFormDto dto = new MovieFormDto();

        assertThatThrownBy(() -> dbMovieService.createMovie(dto))
                .isInstanceOf(InvalidDtoException.class);
        Assert.assertEquals(countBefore, movieRepository.count());
    }

    @Test
    public void moviePopulatedFromDtoProperlyIntegrationTest() {
        MovieFormDto dto = new MovieFormDto();
        dto.setName(TEST_NAME_2);
        dto.setCamera(TEST_CAMERA);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setDirector(TEST_DIRECTOR);
        dto.setMusic(TEST_MUSIC);

        Movie createdMovie = dbMovieService.createMovie(dto);

        assertThat(createdMovie.getName()).isEqualTo(dto.getName());
        assertThat(createdMovie.getDirector()).isEqualTo(dto.getDirector());
        assertThat(createdMovie.getCamera()).isEqualTo(dto.getCamera());
        assertThat(createdMovie.getMusic()).isEqualTo(dto.getMusic());
        assertThat(createdMovie.getDescription()).isEqualTo(dto.getDescription());
        assertThat(createdMovie.getAverageRating()).isEqualTo(0);
    }

    @Test
    public void getExistingMovieIntegrationTest() {
        Movie movie = dbMovieService.getAllMoviesFromDB().get(0);
        Movie returnedMovie = dbMovieService.getMovie(movie.getId());

        assertThat(returnedMovie.getName()).isEqualTo(TEST_NAME);
        assertThat(returnedMovie.getDirector()).isEqualTo(TEST_DIRECTOR);
        assertThat(returnedMovie.getCamera()).isEqualTo(TEST_CAMERA);
        assertThat(returnedMovie.getMusic()).isEqualTo(TEST_MUSIC);
        assertThat(returnedMovie.getDescription()).isEqualTo(TEST_DESCRIPTION);
        assertThat(returnedMovie.getAverageRating()).isEqualTo(0);
    }

    @Test
    public void getNonExistingMovieIntegrationTest() {
        assertThatThrownBy(() -> dbMovieService.getMovie(NON_EXISTING_MOVIE_ID))
        .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    public void deleteExistingMovieTest() {
        int before = movieRepository.findAll().size();
        Movie movie = dbMovieService.getAllMoviesFromDB().get(0);

        dbMovieService.deleteMovie(movie.getId());

        assertThat(movieRepository.findAll().size()).isEqualTo(before - 1);
    }

    @Test
    public void deleteNotExistingMovieTest() {
        int before = movieRepository.findAll().size();

        assertThatThrownBy(() -> dbMovieService.deleteMovie(NON_EXISTING_MOVIE_ID))
                .isInstanceOf(EmptyResultDataAccessException.class);
        assertThat(movieRepository.findAll().size()).isEqualTo(before);
    }
}

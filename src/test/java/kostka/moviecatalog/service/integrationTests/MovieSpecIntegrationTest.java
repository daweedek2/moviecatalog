package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.MovieSpecificationService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_CAMERA;
import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_DESCRIPTION;
import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_DIRECTOR;
import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_MUSIC;
import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_NAME;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class MovieSpecIntegrationTest {

    public static final String OPERATION_LESS = "<";
    public static final String RATING_FIELD = "rating";
    public static final String OPERATION_LESS_OR_EQUAL = "<=";
    public static final String OPERATION_GREATER_OR_EQUAL = ">=";
    public static final String OPERATION_GREATER = ">";
    public static final String NAME_FIELD = "name";
    public static final String OPERATION_NOT_EQUAL = "!=";
    public static final String OPERATION_EQUAL = "==";
    @Autowired
    MovieSpecificationService service;
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
        MovieDto dto = new MovieDto();
        dto.setName(TEST_NAME);
        dto.setCamera(TEST_CAMERA);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setDirector(TEST_DIRECTOR);
        dto.setMusic(TEST_MUSIC);
        dbMovieService.createMovie(dto);
    }

    @Test
    public void getMoviesEqualFieldIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(TEST_NAME);
        dto.setOperation(OPERATION_EQUAL);
        dto.setField(NAME_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(1);
        assertThat(result).contains(movie);
    }

    @Test
    public void getMoviesNotEqualFieldIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(TEST_NAME);
        dto.setOperation(OPERATION_NOT_EQUAL);
        dto.setField(NAME_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(0);
    }

    @Test
    public void getMoviesGreaterRatingIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        movie.setAverageRating(5);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(4);
        dto.setOperation(OPERATION_GREATER);
        dto.setField(RATING_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(1);
        assertThat(result).contains(movie);
    }

    @Test
    public void getMoviesGreaterOrEqualRatingIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        movie.setAverageRating(5);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(5);
        dto.setOperation(OPERATION_GREATER_OR_EQUAL);
        dto.setField(RATING_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(1);
        assertThat(result).contains(movie);
    }

    @Test
    public void getMoviesLessRatingIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        movie.setAverageRating(5);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(4);
        dto.setOperation(OPERATION_LESS);
        dto.setField(RATING_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(0);
        assertThat(result).doesNotContain(movie);
    }

    @Test
    public void getMoviesLessOrEqualRatingIntegrationTest() {
        Movie movie = movieRepository.findAll().get(0);
        movie.setAverageRating(5);
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setValue(4);
        dto.setOperation(OPERATION_LESS_OR_EQUAL);
        dto.setField(RATING_FIELD);

        List<Movie> result = service.getMoviesWithCriteria(dto);

        assertThat(result).size().isEqualTo(0);
        assertThat(result).doesNotContain(movie);
    }
}

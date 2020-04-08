package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class DbMovieServiceIntegrationTest {
    public static final String TEST_NAME = "testName";
    private static final String TEST_CAMERA = "testCamera";
    private static final String TEST_DESCRIPTION = "testDescription";
    private static final String TEST_DIRECTOR = "test¨Director";
    private static final String TEST_MUSIC = "testMusic";
    public static final long NON_EXISTING_MOVIE_ID = 100L;

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
    public void createMovieIntegrationTest() {
        long countBefore = movieRepository.count();
        MovieDto dto = new MovieDto();
        dto.setName(TEST_NAME);
        dbMovieService.createMovie(dto);

        Assert.assertEquals(countBefore + 1, movieRepository.count());
        Assertions.assertThat(movieRepository.count()).isEqualTo(countBefore + 1);
    }

    @Test
    public void doNotCreateMovieWithEmptyDtoIntegrationTest() {
        long countBefore = movieRepository.count();
        MovieDto dto = new MovieDto();

        Assertions.assertThatThrownBy(() -> dbMovieService.createMovie(dto))
                .isInstanceOf(InvalidDtoException.class);
        Assert.assertEquals(countBefore, movieRepository.count());
    }

    @Test
    public void moviePopulatedFromDtoProperlyIntegrationTest() {
        Movie movie = new Movie(1L, TEST_NAME, TEST_DIRECTOR, TEST_CAMERA, TEST_CAMERA, TEST_DESCRIPTION, 0);
        MovieDto dto = new MovieDto();
        dto.setName(TEST_NAME);
        dto.setCamera(TEST_CAMERA);
        dto.setDescription(TEST_DESCRIPTION);
        dto.setDirector(TEST_DIRECTOR);
        dto.setMusic(TEST_MUSIC);

        Movie createdMovie = dbMovieService.createMovie(dto);

        Assertions.assertThat(createdMovie).isEqualToComparingOnlyGivenFields(movie);
    }

    @Test
    public void getExistingMovieIntegrationTest() {
        Movie movie = new Movie(1L, TEST_NAME, TEST_DIRECTOR, TEST_CAMERA, TEST_CAMERA, TEST_DESCRIPTION, 0);
        Movie returnedMovie = dbMovieService.getMovie(movie.getId());

        Assertions.assertThat(returnedMovie).isEqualToComparingOnlyGivenFields(movie);
    }

    @Test
    public void getNonExistingMovieIntegrationTest() {
        Assertions.assertThatThrownBy(() -> dbMovieService.getMovie(NON_EXISTING_MOVIE_ID))
        .isInstanceOf(MovieNotFoundException.class);
    }
}

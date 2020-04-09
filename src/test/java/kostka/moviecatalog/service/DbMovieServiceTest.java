package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;

@RunWith(MockitoJUnitRunner.class)
public class DbMovieServiceTest {
    private static final String TEST_NAME = "TestName";
    private static final String TEST_NAME_2 = "TestName2";
    public static final String ALL_MOVIES_KEY = "all-movies";
    private static final String TOP_MOVIES_KEY = "top-movies";
    private static final String LATEST_MOVIES_KEY = "latest-movies";

    @InjectMocks
    DbMovieService dbMovieService;

    @Mock
    MovieRepository movieRepository;

    @Mock
    EsMovieService esMovieService;

    @Mock
    RedisService redisService;

    private Generator generator = new Generator();

    @Test
    public void createMovieValidDtoTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        MovieDto dto = generator.createValidMovieDto(TEST_NAME);
        Mockito.when(movieRepository.save(any())).thenReturn(movie);

        Movie result = dbMovieService.createMovie(dto);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void createMovieInvalidDtoTest() {
        MovieDto dto = new MovieDto();

        assertThatThrownBy(() -> dbMovieService.createMovie(dto))
                .isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void saveMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Mockito.when(movieRepository.save(any())).thenReturn(movie);

        Movie result = dbMovieService.saveMovie(movie);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void getExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

        Movie result = dbMovieService.getMovie(1L);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void getNonExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Mockito.when(movieRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dbMovieService.getMovie(1L))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    public void fullTextSearchFromESTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Long> ids = Arrays.asList(movie.getId(), movie2.getId());
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(esMovieService.fullTextSearch(any())).thenReturn(anyList());
        Mockito.when(movieRepository.findByIdInOrderByIdDesc(ids)).thenReturn(movies);

        List<Movie> result = dbMovieService.fullTextSearch("test");

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void fullTextSearchNoResultFromESTest() {
        List<Long> ids = Arrays.asList(1L, 2L);

        Mockito.when(esMovieService.fullTextSearch(any())).thenReturn(anyList());
        Mockito.when(movieRepository.findByIdInOrderByIdDesc(ids)).thenReturn(Collections.emptyList());

        List<Movie> result = dbMovieService.fullTextSearch("test");

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = dbMovieService.getAllMoviesFromDB();

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void getAllMoviesFromDBIsEmptyTest() {
        Mockito.when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<Movie> result = dbMovieService.getAllMoviesFromDB();

        assertThat(result).isEmpty();
    }

    @Test
    public void getTop5RatingMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(movieRepository.findTop5ByOrderByRatingDesc()).thenReturn(movies);

        List<Movie> result = dbMovieService.getTop5RatingMoviesFromDB();

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void get5LatestMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(movieRepository.findTop5ByOrderByIdDesc()).thenReturn(movies);

        List<Movie> result = dbMovieService.get5LatestMoviesFromDB();

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void getAllMoviesFromRedisCacheTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(redisService.getMoviesWithKey(ALL_MOVIES_KEY)).thenReturn(movies.toString());

        String result = redisService.getMoviesWithKey(ALL_MOVIES_KEY);

        assertThat(result).isEqualTo(movies.toString());
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void getTop5MoviesFromRedisCacheTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(redisService.getMoviesWithKey(TOP_MOVIES_KEY)).thenReturn(movies.toString());

        String result = redisService.getMoviesWithKey(TOP_MOVIES_KEY);

        assertThat(result).isEqualTo(movies.toString());
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void get5LatestMoviesFromRedisCacheTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        Mockito.when(redisService.getMoviesWithKey(LATEST_MOVIES_KEY)).thenReturn(movies.toString());

        String result = redisService.getMoviesWithKey(LATEST_MOVIES_KEY);

        assertThat(result).isEqualTo(movies.toString());
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }
}

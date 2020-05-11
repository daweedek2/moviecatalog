package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;

import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_NAME;
import static kostka.moviecatalog.service.integrationTests.DbMovieServiceIntegrationTest.TEST_NAME_2;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(MockitoJUnitRunner.class)
public class CacheServiceTest {

    public static final String ALL_MOVIES_KEY = "all-movies";
    public static final String TOP_MOVIES_KEY = "top-movies";
    public static final String LATEST_MOVIES_KEY = "latest-movies";

    @Mock
    RedisService redisService;

    @InjectMocks
    CacheService cacheService;

    private Generator generator = new Generator();

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

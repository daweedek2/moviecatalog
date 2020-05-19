package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.dto.MovieListDto;
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

    private ObjectMapper mapper = new ObjectMapper();

    @Test
    public void getAllMoviesFromRedisCacheTest() throws JsonProcessingException {
        String moviesJson = createMoviesJson();
        Mockito.when(redisService.getMoviesWithKey(ALL_MOVIES_KEY)).thenReturn(moviesJson);

        String result = redisService.getMoviesWithKey(ALL_MOVIES_KEY);

        assertThat(result).isEqualTo(moviesJson);
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void getTop5MoviesFromRedisCacheTest() throws JsonProcessingException{
        String moviesJson = createMoviesJson();
        Mockito.when(redisService.getMoviesWithKey(TOP_MOVIES_KEY)).thenReturn(moviesJson);

        String result = redisService.getMoviesWithKey(TOP_MOVIES_KEY);

        assertThat(result).isEqualTo(moviesJson);
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void get5LatestMoviesFromRedisCacheTest() throws JsonProcessingException {
        String moviesJson = createMoviesJson();
        Mockito.when(redisService.getMoviesWithKey(LATEST_MOVIES_KEY)).thenReturn(moviesJson);

        String result = redisService.getMoviesWithKey(LATEST_MOVIES_KEY);

        assertThat(result).isEqualTo(moviesJson);
        assertThat(result).contains(TEST_NAME, TEST_NAME_2);
    }

    private String createMoviesJson() throws JsonProcessingException{
        MovieListDto movie = new MovieListDto();
        movie.setName(TEST_NAME);
        MovieListDto movie2 = new MovieListDto();
        movie2.setName(TEST_NAME_2);
        List<MovieListDto> movies = Arrays.asList(movie, movie2);
        return mapper.writeValueAsString(movies);
    }
}

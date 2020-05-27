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
import static org.mockito.ArgumentMatchers.eq;

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
        String moviesJson = create2MoviesJson();
        Mockito.when(redisService.getMoviesWithKey(eq(ALL_MOVIES_KEY))).thenReturn(moviesJson);

        List<MovieListDto> result = cacheService.getMoviesFromCacheWithKey(ALL_MOVIES_KEY);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting(
                "name").containsExactlyInAnyOrder(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void getTop5MoviesFromRedisCacheTest() throws JsonProcessingException{
        String moviesJson = create2MoviesJson();
        Mockito.when(redisService.getMoviesWithKey(eq(TOP_MOVIES_KEY))).thenReturn(moviesJson);

        List<MovieListDto> result = cacheService.getMoviesFromCacheWithKey(TOP_MOVIES_KEY);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting(
                "name").containsExactlyInAnyOrder(TEST_NAME, TEST_NAME_2);
    }

    @Test
    public void get5LatestMoviesFromRedisCacheTest() throws JsonProcessingException {
        String moviesJson = create2MoviesJson();
        Mockito.when(redisService.getMoviesWithKey(eq(LATEST_MOVIES_KEY))).thenReturn(moviesJson);

        List<MovieListDto> result = cacheService.getMoviesFromCacheWithKey(LATEST_MOVIES_KEY);

        assertThat(result.size()).isEqualTo(2);
        assertThat(result).extracting(
                "name").containsExactly(TEST_NAME, TEST_NAME_2);
    }

    private String create2MoviesJson() throws JsonProcessingException{
        MovieListDto movie = createTestMovieListDto(TEST_NAME);
        MovieListDto movie2 = createTestMovieListDto(TEST_NAME_2);
        List<MovieListDto> movies = Arrays.asList(movie, movie2);
        return mapper.writeValueAsString(movies);
    }

    private MovieListDto createTestMovieListDto(final String name) {
        MovieListDto dto = new MovieListDto();
        dto.setName(name);
        return dto;
    }
}

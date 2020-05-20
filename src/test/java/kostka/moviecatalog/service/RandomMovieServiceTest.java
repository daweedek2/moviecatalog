package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.exception.NoMoviesInDbException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RandomMovieServiceTest {
    public static final long TEST_ID_1 = 1L;
    @InjectMocks
    private RandomMovieService randomMovieService;

    @Mock
    private CacheService cacheService;

    @Test
    public void generateRandomIdNoMoviesInDbTest() {
        when(cacheService.getMoviesFromCacheWithKey(any())).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> randomMovieService.getRandomMovieIdFromAllMovies())
                .isInstanceOf(NoMoviesInDbException.class);
    }

    @Test
    public void generateRandomMovieIdSuccessfulTest() {
        MovieListDto movie = new MovieListDto();
        movie.setId(TEST_ID_1);
        when(cacheService.getMoviesFromCacheWithKey(any())).thenReturn(Collections.singletonList(movie));

        Long randomId = randomMovieService.getRandomMovieIdFromAllMovies();

        assertThat(randomId).isEqualTo(TEST_ID_1);
    }
}

package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceTest {
    public static final String TEST_NAME = "TestName";
    public static final long TEST_ID = 1L;
    public static final int TEST_RATING = 1;
    @InjectMocks
    RatingService ratingService;

    @Mock
    DbMovieService dbMovieService;

    @Mock
    private StatisticService statisticService;

    private Generator generator = new Generator();

    @Test
    public void createRatingValidDtoExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        movie.setId(TEST_ID);
        RatingDto dto = generator.createValidRatingDto(TEST_ID, TEST_RATING);

        Mockito.when(dbMovieService.getMovie(TEST_ID)).thenReturn(movie);
        Mockito.when(dbMovieService.saveMovie(movie)).thenReturn(movie);

        Movie result = ratingService.createRating(dto);

        assertThat(result.getRating()).isEqualTo(TEST_RATING);
        assertThat(result.getId()).isEqualTo(TEST_ID);
    }

    @Test
    public void createRatingInvalidDtoExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        movie.setId(TEST_ID);
        RatingDto dto = new RatingDto();
        dto.setRating(TEST_RATING);

        assertThatThrownBy(() -> ratingService.createRating(dto))
                .isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void createRatingValidDtoNonExistingMovieTest() {
        RatingDto dto = generator.createValidRatingDto(TEST_ID, TEST_RATING);

        Mockito.when(dbMovieService.getMovie(TEST_ID)).thenThrow(MovieNotFoundException.class);

        assertThatThrownBy(() -> ratingService.createRating(dto))
                .isInstanceOf(MovieNotFoundException.class);
    }
}

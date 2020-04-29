package kostka.ratingservice;

import kostka.ratingservice.dto.RatingDto;
import kostka.ratingservice.exception.InvalidDtoException;
import kostka.ratingservice.model.Rating;
import kostka.ratingservice.repository.RatingRepository;
import kostka.ratingservice.service.RatingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static kostka.ratingservice.RatingServiceIntegrationTest.TEST_GREATER_RATING;
import static kostka.ratingservice.RatingServiceIntegrationTest.TEST_ID_1;
import static kostka.ratingservice.RatingServiceIntegrationTest.TEST_LESS_RATING;
import static kostka.ratingservice.RatingServiceIntegrationTest.TEST_VALID_RATING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RatingServiceTest {

    @InjectMocks
    RatingService ratingService;

    @Mock
    RatingRepository ratingRepository;

    @Test
    public void createRatingValidDtoTest() {
        Rating rating = new Rating(TEST_ID_1, TEST_ID_1, TEST_ID_1, TEST_VALID_RATING);
        RatingDto dto = new RatingDto(TEST_ID_1, TEST_ID_1, TEST_VALID_RATING);
        when(ratingRepository.save(any())).thenReturn(rating);

        Rating result = ratingService.createRating(dto);

        assertThat(result).isEqualTo(rating);
    }

    @Test
    public void creatRatingInvalidEmptyMovieIdDtoTest() {
        RatingDto dto = new RatingDto();
        dto.setAuthorId(TEST_ID_1);
        dto.setRatingValue(TEST_VALID_RATING);

        assertThatThrownBy(() -> ratingService.createRating(dto)).isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void creatRatingInvalidRatingUnderTheRangeDtoTest() {
        RatingDto dto = new RatingDto(TEST_ID_1, TEST_ID_1, TEST_LESS_RATING);

        assertThatThrownBy(() -> ratingService.createRating(dto)).isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void creatRatingInvalidRatingAboveTheRangeDtoTest() {
        RatingDto dto = new RatingDto(TEST_ID_1, TEST_ID_1, TEST_GREATER_RATING);

        assertThatThrownBy(() -> ratingService.createRating(dto)).isInstanceOf(InvalidDtoException.class);
    }
}
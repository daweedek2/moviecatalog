package kostka.ratingservice;

import kostka.ratingservice.dto.RatingDto;
import kostka.ratingservice.model.Rating;
import kostka.ratingservice.repository.RatingRepository;
import kostka.ratingservice.service.RatingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = RatingServiceApplication.class)
@Transactional
public class RatingServiceIntegrationTest {
    public static final int TEST_VALID_RATING = 5;
    public static final int TEST_LESS_RATING = 0;
    public static final int TEST_GREATER_RATING = 11;
    public static final long TEST_ID_1 = 1L;
    public static final long TEST_ID_2 = 2L;
    @Autowired
    private RatingRepository ratingRepository;
    @Autowired
    private RatingService ratingService;

    @Test
    public void createRatingValidDtoIntegrationTest() {
        RatingDto dto = new RatingDto(TEST_ID_1, TEST_ID_2, TEST_VALID_RATING);
        int before = ratingRepository.findAll().size();

        Rating rating = ratingService.createRating(dto);

        assertThat(rating.getMovieId()).isEqualTo(TEST_ID_1);
        assertThat(rating.getAuthorId()).isEqualTo(TEST_ID_2);
        assertThat(rating.getRatingValue()).isEqualTo(TEST_VALID_RATING);
        assertThat(ratingRepository.findAll().size()).isEqualTo(before + 1);
    }

    @Test
    public void getRatingsOfMovieIntegrationTest() {
        RatingDto dto1 = new RatingDto(TEST_ID_1, TEST_ID_2, TEST_VALID_RATING);
        RatingDto dto2 = new RatingDto(TEST_ID_1, TEST_ID_2, TEST_VALID_RATING);
        RatingDto dto3 = new RatingDto(TEST_ID_2, TEST_ID_2, TEST_VALID_RATING);

        Rating rating1 = ratingService.createRating(dto1);
        Rating rating2 = ratingService.createRating(dto2);
        Rating rating3 = ratingService.createRating(dto3);

        List<Rating> result = ratingService.getRatingsForMovie(TEST_ID_1);

        assertThat(result).contains(rating1, rating2);
        assertThat(result).doesNotContain(rating3);
    }
}

package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.RatingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class RatingServiceIntegrationTest {
    private static final long NON_EXISTING_MOVIE_ID = 100L;
    private static final int RATING = 10;
    private static final String TEST_NAME = "test_name";
    @Autowired
    private DbMovieService dbMovieService;
    @Autowired
    private RatingService ratingService;
    @MockBean
    EsMovieService esMovieService;
    @MockBean
    MovieElasticSearchRepository movieElasticSearchRepository;

    @Test
    public void createRatingIntegrationTest() {
        MovieDto movieDto = new MovieDto();
        movieDto.setName(TEST_NAME);
        Movie movie = dbMovieService.createMovie(movieDto);
        RatingDto ratingDto = new RatingDto();
        ratingDto.setId(movie.getId());
        ratingDto.setRating(RATING);

        ratingService.createRating(ratingDto);

        assertThat(movie.getRating()).isEqualTo(10);
    }

    @Test
    public void createRatingNonExistingMovieIntegrationTest() throws Exception {
        RatingDto ratingDto = new RatingDto();
        ratingDto.setId(NON_EXISTING_MOVIE_ID);
        ratingDto.setRating(RATING);

        assertThatThrownBy(() -> ratingService.createRating(ratingDto))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    public void nonCreateRatingInvalidDtoIntegrationTest() {
        MovieDto movieDto = new MovieDto();
        movieDto.setName(TEST_NAME);
        Movie movie = dbMovieService.createMovie(movieDto);
        RatingDto ratingDto = new RatingDto();

        assertThatThrownBy(() -> ratingService.createRating(ratingDto))
                .isInstanceOf(InvalidDtoException.class);
        assertThat(movie.getRating()).isEqualTo(0);
    }
}

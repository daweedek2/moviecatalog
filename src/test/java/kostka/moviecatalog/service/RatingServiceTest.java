package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
import kostka.moviecatalog.entity.Movie;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MovieCatalogApplication.class)
@Transactional
public class RatingServiceTest {
    private RatingService ratingService;
    private MovieService movieService;

    @Autowired
    public RatingServiceTest(RatingService ratingService, MovieService movieService) {
        this.ratingService = ratingService;
        this.movieService = movieService;
    }

    @Test
    public void createRatingTest() {
        Movie movie = movieService.createMovie("Seven");
        ratingService.createRating(movie.getId(), 10);

        Assertions.assertEquals(10, movie.getRating());
    }
}

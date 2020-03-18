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
    private MovieServiceImpl movieServiceImpl;

    @Autowired
    public RatingServiceTest(RatingService ratingService, MovieServiceImpl movieServiceImpl) {
        this.ratingService = ratingService;
        this.movieServiceImpl = movieServiceImpl;
    }

    @Test
    public void createRatingTest() {
        Movie movie = movieServiceImpl.createMovie("Seven");
        ratingService.createRating(movie.getId(), 10);

        Assertions.assertEquals(10, movie.getRating());
    }
}

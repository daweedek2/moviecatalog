package kostka.moviecatalog.service;

import kostka.moviecatalog.MovieCatalogApplication;
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
    private DbMovieService dbMovieService;

    @Autowired
    public RatingServiceTest(RatingService ratingService, DbMovieService dbMovieService) {
        this.ratingService = ratingService;
        this.dbMovieService = dbMovieService;
    }

//    @Test
//    public void createRatingTest() {
//        MovieDto movieDto = new MovieDto();
//        movieDto.setName("test");
//        Movie movie = dbMovieService.createMovie(movieDto);
//        RatingDto ratingDto = new RatingDto();
//        ratingDto.setId(movie.getId());
//        ratingDto.setRating(10);
//        ratingService.createRating(ratingDto);
//
//        Assertions.assertEquals(10, movie.getRating());
//    }
}

package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.entity.AverageRating;
import kostka.moviecatalog.entity.MovieRating;
import kostka.moviecatalog.entity.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Service which communicates with external microservice (RatingService) via rest template.
 */
@Service
public class ExternalRatingService {
    private RestTemplate restTemplate;
    private DbMovieService dbMovieService;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRatingService.class);
    private static final String RATING_URL_SERVICE_DISCOVERY = "http://rating-service/rating/";
    private static final String AVERAGE_RATING_URL_SERVICE_DISCOVERY = "http://rating-service/rating/average/";

    @Autowired
    public ExternalRatingService(final RestTemplate restTemplate, final DbMovieService dbMovieService) {
        this.restTemplate = restTemplate;
        this.dbMovieService = dbMovieService;
    }

    /**
     * Gets all ratings for specific movie from the microservice Rating Service.
     * @param movieId id of the movie.
     * @return List of ratings.
     */
    @HystrixCommand(fallbackMethod = "getRatingsFromRatingServiceFallback")
    public List<Rating> getRatingsFromRatingService(final Long movieId) {
        LOGGER.info("Getting ratings from Rating service.");
        MovieRating ratingsResponse = restTemplate.getForObject(
                RATING_URL_SERVICE_DISCOVERY + movieId,
                MovieRating.class);

        return Objects.requireNonNull(ratingsResponse).getMovieRatings();
    }

    /**
     * Gets average rating for specific movie from the microservice Rating Service.
     * @param movieId id of the movie.
     * @return value of the average rating.
     */
    @HystrixCommand(fallbackMethod = "getAverageRatingFromRatingServiceFallback")
    public double getAverageRatingFromRatingService(final Long movieId) {
        LOGGER.info("Getting average rating from Rating service.");
        AverageRating ratingsResponse = restTemplate.getForObject(
                AVERAGE_RATING_URL_SERVICE_DISCOVERY + movieId,
                AverageRating.class);

        return Objects.requireNonNull(ratingsResponse).getAverageRatingValue();
    }

    /**
     * Fallback method which returns list of default rating when microservice Rating Service is not available.
     * @param movieId id of the movie.
     * @return default list of one rating with default values.
     */
    public List<Rating> getRatingsFromRatingServiceFallback(final Long movieId) {
        LOGGER.info("Rating Service is down - return default rating List.");
        Rating randomRating = new Rating();
        randomRating.setMovieId(movieId);
        randomRating.setRatingId(999L);
        randomRating.setRatingValue(0);
        randomRating.setAuthorId(999L);
        return Collections.singletonList(randomRating);
    }

    /**
     * Fallback method which returns average rating when microservice Rating Service is not available.
     * @param movieId id of the movie.
     * @return default value of the average rating.
     */
    public double getAverageRatingFromRatingServiceFallback(final Long movieId) {
        LOGGER.info("Rating Service is down - return the stored value in DB for average Rating.");
        return dbMovieService.getMovie(movieId).getAverageRating();
    }
}

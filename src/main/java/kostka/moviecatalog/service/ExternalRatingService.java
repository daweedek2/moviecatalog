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

@Service
public class ExternalRatingService {
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRatingService.class);
    private static final String RATING_URL_SERVICE_DISCOVERY = "http://rating-service/rating/";
    private static final String AVERAGE_RATING_URL_SERVICE_DISCOVERY = "http://rating-service/rating/average/";

    @Autowired
    public ExternalRatingService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @HystrixCommand(fallbackMethod = "getRatingsFromRatingServiceFallback")
    public List<Rating> getRatingsFromRatingService(final Long movieId) {
        LOGGER.info("Getting ratings from Rating service.");
        MovieRating ratingsResponse = restTemplate.getForObject(
                RATING_URL_SERVICE_DISCOVERY + movieId,
                MovieRating.class);

        return Objects.requireNonNull(ratingsResponse).getMovieRatings();
    }

    @HystrixCommand(fallbackMethod = "getAverageRatingFromRatingServiceFallback")
    public double getAverageRatingFromRatingService(final Long movieId) {
        LOGGER.info("Getting average rating from Rating service.");
        AverageRating ratingsResponse = restTemplate.getForObject(
                AVERAGE_RATING_URL_SERVICE_DISCOVERY + movieId,
                AverageRating.class);

        return Objects.requireNonNull(ratingsResponse).getAverageRatingValue();
    }

    public List<Rating> getRatingsFromRatingServiceFallback(final Long movieId) {
        LOGGER.info("Rating Service is down - return default rating List.");
        Rating randomRating = new Rating();
        randomRating.setMovieId(movieId);
        randomRating.setRatingId(999L);
        randomRating.setRatingValue(0);
        randomRating.setAuthorId(999L);
        return Collections.singletonList(randomRating);
    }

    public double getAverageRatingFromRatingServiceFallback(final Long movieId) {
        LOGGER.info("Rating Service is down - return default average Rating.");
        return movieId;
    }
}

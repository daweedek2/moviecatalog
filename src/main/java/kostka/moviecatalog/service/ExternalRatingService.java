package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.MovieRating;
import kostka.moviecatalog.entity.Rating;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ExternalRatingService {
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalRatingService.class);
    private static final String RATING_URL_SERVICE_DISCOVERY = "http://rating-service/rating/";

    @Autowired
    public ExternalRatingService(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Rating> getRatingsFromRatingService(final Long movieId) {
        LOGGER.info("Getting ratings from Rating service.");
        MovieRating ratingsResponse = restTemplate.getForObject(
                RATING_URL_SERVICE_DISCOVERY + movieId,
                MovieRating.class);

        return Objects.requireNonNull(ratingsResponse).getMovieRatings();
    }

    public double getAverageRatingFromRatingService(final Long movieId) {
        LOGGER.info("Getting average rating from Rating service.");
        MovieRating ratingsResponse = restTemplate.getForObject(
                RATING_URL_SERVICE_DISCOVERY + movieId,
                MovieRating.class);
        List<Rating> ratings = Objects.requireNonNull(ratingsResponse).getMovieRatings();

        return ratings.stream()
                .mapToDouble(Rating::getRatingValue)
                .average()
                .orElse(0.0);
    }
}

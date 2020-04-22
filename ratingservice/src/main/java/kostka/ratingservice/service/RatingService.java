package kostka.ratingservice.service;

import kostka.ratingservice.dto.RatingDto;
import kostka.ratingservice.exception.InvalidDtoException;
import kostka.ratingservice.model.AverageRating;
import kostka.ratingservice.model.Rating;
import kostka.ratingservice.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RatingService {
    public static final int MAX_RATING_VALUE = 10;
    public static final int MIN_RATING_VALUE = 1;
    private RatingRepository ratingRepository;

    @Autowired
    public RatingService(final RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    /***
     * Method which creates new Rating in db based on the data in the dto.
     * @param dto holds the data for the new Rating.
     * @return newly created Rating.
     */
    public Rating createRating(final RatingDto dto) {
        Long movieId = dto.getMovieId();
        int ratingValue = dto.getRatingValue();
        if (movieId == null || ratingValue < MIN_RATING_VALUE || ratingValue > MAX_RATING_VALUE) {
            throw new InvalidDtoException();
        }
        Rating rating = new Rating();
        rating.setMovieId(dto.getMovieId());
        rating.setAuthorId(dto.getUserId());
        rating.setRatingValue(dto.getRatingValue());
        return ratingRepository.save(rating);
    }

    /***
     * Method which returns list of rating for one specific movie.
     * @param movieId id of the desired movie.
     * @return list of all ratings for desired movie.
     */
    public List<Rating> getRatingsForMovie(final Long movieId) {
        return ratingRepository.findAllByMovieId(movieId);
    }

    /***
     * Method which gets average rating value for one specific movie.
     * @param movieId id of the desired movie.
     * @return AverageRating entity which holds the average rating value.
     */
    public AverageRating getAverageRatingForMovie(final Long movieId) {
        AverageRating averageRating = new AverageRating();
        averageRating.setAverageRatingValue(ratingRepository.findAllByMovieId(movieId)
                .stream()
                .mapToDouble(Rating::getRatingValue)
                .average()
                .orElse(0.0));
        return averageRating;
    }
}

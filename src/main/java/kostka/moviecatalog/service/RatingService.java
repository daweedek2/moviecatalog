package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.dto.RatingDto.MAX_RATING_VALUE;
import static kostka.moviecatalog.dto.RatingDto.MIN_RATING_VALUE;

@Service
public class RatingService {
    static final Logger LOGGER = LoggerFactory.getLogger(RatingService.class);
    private DbMovieService dbMovieService;

    @Autowired
    public RatingService(final DbMovieService dbMovieService) {
        this.dbMovieService = dbMovieService;
    }

    public Movie createRating(final RatingDto dto) {
        Long movieId = dto.getMovieId();
        int rating = dto.getRatingValue();
        if (!isValidDto(dto)) {
            LOGGER.error("Dto has no ID specified or rating is out of range");
            throw new InvalidDtoException();
        }
        try {
            Movie movie = dbMovieService.getMovie(movieId);
            movie.setRating(rating);
            return dbMovieService.saveMovie(movie);
        } catch (Exception e) {
            LOGGER.error("Cannot create rating, movie with ID '{}' not found", movieId, e);
            throw new MovieNotFoundException();
        }
    }

    private boolean isValidDto(final RatingDto dto) {
        Long movieId = dto.getMovieId();
        int rating = dto.getRatingValue();
        return rating >= MIN_RATING_VALUE && rating <= MAX_RATING_VALUE && movieId != null;
    }
}

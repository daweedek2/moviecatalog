package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    static final Logger LOGGER = LoggerFactory.getLogger(RatingService.class);
    private DbMovieService dbMovieService;

    @Autowired
    public RatingService(final DbMovieService dbMovieService) {
        this.dbMovieService = dbMovieService;
    }

    public Movie createRating(final RatingDto dto) {
        Long movieId = dto.getId();
        if (movieId == null) {
            LOGGER.error("Dto has no ID specified");
            throw new InvalidDtoException();
        }

        Movie movie = dbMovieService.getMovie(movieId);
        movie.setRating(dto.getRating());
        return dbMovieService.saveMovie(movie);
    }
}

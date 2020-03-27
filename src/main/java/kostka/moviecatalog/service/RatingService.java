package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    private DbMovieService dbMovieService;

    @Autowired
    public RatingService(final DbMovieService dbMovieService) {
        this.dbMovieService = dbMovieService;
    }

    public Movie createRating(final RatingDto dto) {
        Movie movie = dbMovieService.getMovie(dto.getId());
        movie.setRating(dto.getRating());
        return dbMovieService.saveMovie(movie);
    }
}

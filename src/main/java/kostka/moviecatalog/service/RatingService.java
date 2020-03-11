package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RatingService {
    private MovieService movieService;

    @Autowired
    public RatingService(final MovieService movieService) {
        this.movieService = movieService;
    }

    public Movie createRating(final Long movieId, final int value) {
        Optional<Movie> movieOptional = movieService.getMovie(movieId);
        if (movieOptional.isEmpty()) {
            return null;
        }
        Movie movie = movieOptional.get();
        movie.setRating(value);
        return movieService.saveMovie(movie);
    }
}

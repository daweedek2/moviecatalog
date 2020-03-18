package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
public class RatingService {
    private MovieServiceImpl movieServiceImpl;

    @Autowired
    public RatingService(final MovieServiceImpl movieServiceImpl) {
        this.movieServiceImpl = movieServiceImpl;
    }

    public Movie createRating(final Long movieId, final int value) {
        Optional<Movie> movieOptional = movieServiceImpl.getMovie(movieId);
        if (movieOptional.isEmpty()) {
            return null;
        }
        Movie movie = movieOptional.get();
        movie.setRating(value);
        return movieServiceImpl.saveMovie(movie);
    }
}

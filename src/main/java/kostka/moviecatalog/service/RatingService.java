package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RatingService {
    private MovieServiceImpl movieServiceImpl;

    @Autowired
    public RatingService(final MovieServiceImpl movieServiceImpl) {
        this.movieServiceImpl = movieServiceImpl;
    }

    public Movie createRating(final Long movieId, final int value) {
        Movie movie = movieServiceImpl.getMovie(movieId);
        movie.setRating(value);
        return movieServiceImpl.saveMovie(movie);
    }
}

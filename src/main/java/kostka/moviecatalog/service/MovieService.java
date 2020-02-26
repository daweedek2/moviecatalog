package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MovieService {
    private MovieRepository movieRepository;

    @Autowired
    public MovieService(final MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public Movie createMovie(final String name) {
        Movie movie = new Movie();
        movie.setName(name);
        return saveMovie(movie);
    }

    public Movie saveMovie(final Movie movie) {
        return movieRepository.save(movie);
    }
}

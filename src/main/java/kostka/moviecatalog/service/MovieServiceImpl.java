package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MovieServiceImpl implements MovieService<Movie> {
    private MovieRepository movieRepository;
    private MovieEsServiceImpl movieEsService;

    @Autowired
    public MovieServiceImpl(final MovieRepository movieRepository,
                            final MovieEsServiceImpl movieEsService) {
        this.movieRepository = movieRepository;
        this.movieEsService = movieEsService;
    }

    @Override
    public Movie createMovie(final String name) {
        movieEsService.createMovie(name);
        Movie movie = new Movie();
        movie.setName(name);
        return saveMovie(movie);
    }

    @Override
    public Movie saveMovie(final Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Optional<Movie> getMovie(final Long movieId) {
        return movieRepository.findById(movieId);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }
}

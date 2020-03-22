package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        Movie movie = new Movie();
        movie.setName(name);
        Movie createdMovie = movieRepository.save(movie);
        movieEsService.saveMovie(
                new EsMovie(
                        createdMovie.getId(),
                        createdMovie.getName(),
                        "davidDIrector",
                        createdMovie.getDescription()
                )
        );
        return createdMovie;
    }

    @Override
    public Movie saveMovie(final Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie getMovie(final Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isEmpty()) {
            return null;
        }
        return movie.get();
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> fullTextSearch(final String searchTerm) {
        List<EsMovie> esMovies = movieEsService.fullTextSearch(searchTerm);
        return esMovies.stream().map(esMovie -> getMovie(esMovie.getId())).collect(Collectors.toList());
    }
}

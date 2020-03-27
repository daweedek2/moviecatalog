package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MovieEsServiceImpl implements MovieService<EsMovie> {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    private MovieElasticSearchRepository movieElasticSearchRepository;
    private MovieRepository movieRepository;

    @Autowired
    public MovieEsServiceImpl(final MovieElasticSearchRepository movieElasticSearchRepository,
                              final MovieRepository movieRepository) {
        this.movieElasticSearchRepository = movieElasticSearchRepository;
        this.movieRepository = movieRepository;
    }

    @Override
    public EsMovie createMovie(final String name) {
        List<Movie> dbMovies = movieRepository.findMovieByName(name);
        if (dbMovies.isEmpty()) {
            return null;
        }
        Movie dbMovie = dbMovies.get(0);
        EsMovie esMovie = new EsMovie();
        esMovie.setId(dbMovie.getId());
        esMovie.setName(dbMovie.getName());
        esMovie.setDirector(dbMovie.getDirector() + "ES");
        esMovie.setDescription(dbMovie.getDescription() + "ES");
        LOGGER.info("Movie with name '{}' is created in ElasticSearch", esMovie.getName());
        return saveMovie(esMovie);
    }

    @Override
    public EsMovie saveMovie(final EsMovie esMovie) {
        return movieElasticSearchRepository.save(esMovie);
    }

    @Override
    public EsMovie getMovie(final Long movieId) {
        return new EsMovie();
    }

    @Override
    public List<EsMovie> getAllMovies() {
        Iterable<EsMovie> movies = movieElasticSearchRepository.findAll();
        return StreamSupport.stream(movies.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<EsMovie> get5LatestMovies() {
        return Collections.emptyList();
    }

    @Override
    public List<EsMovie> fullTextSearch(final String term) {
        Iterable<EsMovie> foundMovies = movieElasticSearchRepository.fullTextSearch(term);
        return StreamSupport.stream(foundMovies.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<EsMovie> getTop5RatingMoviesFromDB() {
        return Collections.emptyList();
    }

    @Override
    public List<EsMovie> getTop5RatingMoviesFromCache() {
        return Collections.emptyList();
    }
}

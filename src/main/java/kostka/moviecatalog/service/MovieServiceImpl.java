package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MovieServiceImpl implements MovieService<Movie> {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    private MovieRepository movieRepository;
    private MovieEsServiceImpl movieEsService;
    private RedisService redisService;

    @Autowired
    public MovieServiceImpl(final MovieRepository movieRepository,
                            final MovieEsServiceImpl movieEsService,
                            final RedisService redisService) {
        this.movieRepository = movieRepository;
        this.movieEsService = movieEsService;
        this.redisService = redisService;
    }

    @Override
    public Movie createMovie(final String name) {
        Movie movie = new Movie();
        movie.setName(name);
        movie.setCamera("randomCamera");
        movie.setDescription("randomDescription");
        movie.setMusic("randomMusic");
        movie.setDirector("randomDirector");
        LOGGER.info("DB movie with name '{}' is created in MySQL", movie.getName());
        return movieRepository.save(movie);
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
        List<Long> ids = movieEsService.fullTextSearch(searchTerm)
                .stream()
                .map(EsMovie::getId)
                .collect(Collectors.toList());
        return movieRepository.findByIdInOrderByIdDesc(ids);
    }

    @Override
    public List<Movie> get5LatestMovies() {
        List<String> stringIds = redisService.getLatestMovieIds();
        List<Long> longIds = stringIds.stream().map(Long::valueOf).collect(Collectors.toList());
        return movieRepository.findByIdInOrderByIdDesc(longIds);
    }

    @Override
    public List<Movie> getTop5RatingMoviesFromDB() {
        return movieRepository.findTop5ByOrderByRatingDesc();
    }

    @Override
    public List<Movie> getTop5RatingMoviesFromCache() {
        List<String> stringIds = redisService.getTopRatingMovieIds();
        List<Long> longIds = stringIds.stream().map(Long::valueOf).collect(Collectors.toList());
        return movieRepository.findByIdInOrderByRatingDesc(longIds);
    }
}

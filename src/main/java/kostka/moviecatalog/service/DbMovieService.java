package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Service
public class DbMovieService {
    static final Logger LOGGER = LoggerFactory.getLogger(DbMovieService.class);
    private MovieRepository movieRepository;
    private EsMovieService esMovieService;
    private RedisService redisService;

    @Autowired
    public DbMovieService(final MovieRepository movieRepository,
                          final EsMovieService esMovieService,
                          final RedisService redisService) {
        this.movieRepository = movieRepository;
        this.esMovieService = esMovieService;
        this.redisService = redisService;
    }

    public Movie createMovie(final MovieDto dto) {
        Movie movie = this.populateMovieFromDto(dto);
        LOGGER.info("DB movie with name '{}' is created in MySQL", movie.getName());
        return movieRepository.save(movie);
    }

    public Movie saveMovie(final Movie movie) {
        return movieRepository.save(movie);
    }

    public Movie getMovie(final Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        if (movie.isEmpty()) {
            return null;
        }
        return movie.get();
    }

    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    public List<Movie> fullTextSearch(final String searchTerm) {
        List<Long> ids = esMovieService.fullTextSearch(searchTerm)
                .stream()
                .map(EsMovie::getId)
                .collect(Collectors.toList());
        return movieRepository.findByIdInOrderByIdDesc(ids);
    }

    public List<Movie> get5LatestMoviesFromCache() {
        List<Long> longIds = getMovieIdsFromRedisCache(LATEST_MOVIES_KEY);
        return movieRepository.findByIdInOrderByIdDesc(longIds);
    }

    public List<Movie> getTop5RatingMoviesFromDB() {
        return movieRepository.findTop5ByOrderByRatingDesc();
    }

    public List<Movie> get5LatestMoviesFromDB() {
        return movieRepository.findTop5ByOrderByIdDesc();
    }


    public List<Movie> getTop5RatingMoviesFromCache() {
        List<Long> longIds = getMovieIdsFromRedisCache(TOP_RATING_KEY);
        return movieRepository.findByIdInOrderByRatingDesc(longIds);
    }

    private List<Long> getMovieIdsFromRedisCache(final String key) {
        List<String> stringIds = redisService.getListFromCacheWithKey(key);
        return stringIds.stream().map(Long::valueOf).collect(Collectors.toList());
    }

    private Movie populateMovieFromDto(final MovieDto dto) {
        Movie movie = new Movie();
        movie.setName(dto.getName());
        movie.setDescription(dto.getDescription());
        movie.setDirector(dto.getDirector());
        movie.setMusic(dto.getMusic());
        movie.setCamera(dto.getCamera());
        return movie;
    }
}

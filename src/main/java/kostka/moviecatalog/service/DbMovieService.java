package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class DbMovieService {
    static final Logger LOGGER = LoggerFactory.getLogger(DbMovieService.class);
    public static final long SLEEP_TIME = 5000L;
    private MovieRepository movieRepository;
    private EsMovieService esMovieService;
    private RedisService redisService;
    private StatisticService statisticService;

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public DbMovieService(final MovieRepository movieRepository,
                          final EsMovieService esMovieService,
                          final RedisService redisService,
                          final StatisticService statisticService) {
        this.movieRepository = movieRepository;
        this.esMovieService = esMovieService;
        this.redisService = redisService;
        this.statisticService = statisticService;
    }

    public Movie createMovie(final MovieDto dto) {
        if (dto.getName() == null) {
            LOGGER.error("Movie dto does not contain name.");
            throw new InvalidDtoException();
        }
        Movie movie = this.populateMovieFromDto(dto);
        LOGGER.info("DB movie with name '{}' is created in MySQL", movie.getName());
        return movieRepository.save(movie);
    }

    public Movie saveMovie(@NonNull final Movie movie) {
        statisticService.incrementSyncedDbCounter();
        return movieRepository.save(movie);
    }

    public Movie getMovie(final Long movieId) {
        Optional<Movie> movie = movieRepository.findById(movieId);
        statisticService.incrementSyncedDbCounter();
        if (movie.isEmpty()) {
            throw new MovieNotFoundException();
        }
        return movie.get();
    }

    public List<Movie> fullTextSearch(final String searchTerm) {
        List<Long> ids = esMovieService.fullTextSearch(searchTerm)
                .stream()
                .map(EsMovie::getId)
                .collect(Collectors.toList());
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findByIdInOrderByIdDesc(ids);
    }

    public List<Movie> getAllMoviesFromDB() {
        LOGGER.info("get all from DB");
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findAll();
    }

    public List<Movie> getTop5RatingMoviesFromDB() {
        LOGGER.info("get top 5 from DB");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
            LOGGER.error("sleep error", e);
        }
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findTop5ByOrderByAverageRatingDesc();
    }

    public List<Movie> get5LatestMoviesFromDB() {
        LOGGER.info("get latest 5 from DB");
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findTop5ByOrderByIdDesc();
    }

    public List<Movie> getMoviesFromCacheWithKey(final String key) {
        LOGGER.info("get movies from redis cache with key '{}'", key);
        String json = redisService.getMoviesWithKey(key);
        try {
           return Arrays.asList(mapper.readValue(json, Movie[].class));
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot get movies from json.", e);
        }
        return Collections.emptyList();
    }

    public void deleteMovie(final Long movieId) {
        movieRepository.deleteById(movieId);
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

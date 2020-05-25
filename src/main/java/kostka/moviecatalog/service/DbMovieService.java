package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesConfigValue;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.runtimeconfig.RuntimeConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kostka.moviecatalog.enums.RuntimeConfigurationEnum.VISIBLE_MOVIES;

@Service
public class DbMovieService {
    public static final long SLEEP_TIME = 5000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMovieService.class);
    private static final int DEFAULT_LIMIT = 5;
    private MovieRepository movieRepository;
    private EsMovieService esMovieService;
    private StatisticService statisticService;
    private RuntimeConfigurationService runtimeConfigService;

    @Autowired
    public DbMovieService(final MovieRepository movieRepository,
                          final EsMovieService esMovieService,
                          final StatisticService statisticService,
                          final RuntimeConfigurationService runtimeConfigService) {
        this.movieRepository = movieRepository;
        this.esMovieService = esMovieService;
        this.statisticService = statisticService;
        this.runtimeConfigService = runtimeConfigService;
    }

    public Movie createMovie(final MovieFormDto dto) {
        if (dto.getName() == null) {
            LOGGER.error("Movie dto does not contain name.");
            throw new InvalidDtoException();
        }
        Movie movie = this.populateMovie(dto);
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

    public List<MovieListDto> getAllMoviesForCaching() {
        return this.getAllMoviesFromDB().stream()
                .map(this::fillMovieListDtoWithData)
                .collect(Collectors.toList());
    }

    public List<MovieListDto> getTopRatingMoviesForCaching() {
        final int limit = getLimitValueFromConfig();
        return this.getNTopRatedMoviesFromDB(limit).stream()
                .map(this::fillMovieListDtoWithData)
                .collect(Collectors.toList());
    }

    public List<MovieListDto> getLatestMoviesForCaching() {
        final int limit = getLimitValueFromConfig();
        return this.getNLatestMoviesFromDB(limit).stream()
                .map(this::fillMovieListDtoWithData)
                .collect(Collectors.toList());
    }

    public List<Movie> getNLatestMoviesFromDB(final int limit) {
        LOGGER.info("get latest '{}' from DB", limit);
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findNLatestMovies(limit);
    }

    public List<Movie> getNTopRatedMoviesFromDB(final int limit) {
        LOGGER.info("get '{}' top rated movies from DB", limit);
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
            LOGGER.error("sleep error", e);
        }
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findNTopRatedMovies(limit);
    }

    public void deleteMovie(final Long movieId) {
        movieRepository.deleteById(movieId);
    }

    public MovieListDto fillMovieListDtoWithData(final Movie movie) {
        MovieListDto dto = new MovieListDto();
        dto.setId(movie.getId());
        dto.setName(movie.getName());
        dto.setDescription(movie.getDescription());
        dto.setAverageRating(movie.getAverageRating());
        dto.setForAdults(movie.isForAdults());

        return dto;
    }

    private Movie populateMovie(final MovieFormDto dto) {
        Movie movie = new Movie();
        movie.setName(dto.getName());
        movie.setDescription(dto.getDescription());
        movie.setDirector(dto.getDirector());
        movie.setMusic(dto.getMusic());
        movie.setCamera(dto.getCamera());
        movie.setForAdults(dto.isForAdults());
        return movie;
    }

    private int getLimitValueFromConfig() {
        try {
            VisibleMoviesConfigValue value = runtimeConfigService
                    .getRuntimeConfigurationValue(VISIBLE_MOVIES, VisibleMoviesConfigValue.class);
            return value.getLimit();
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot get Config value", e);
            return DEFAULT_LIMIT;
        }
    }
}

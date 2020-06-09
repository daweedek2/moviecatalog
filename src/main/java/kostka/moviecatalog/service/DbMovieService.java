package kostka.moviecatalog.service;

import kostka.moviecatalog.builders.MovieBuilder;
import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.runtimeconfiguration.VisibleMoviesOptionsRuntimeConfig;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieRepository;
import kostka.moviecatalog.service.runtimeconfiguration.RuntimeConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static kostka.moviecatalog.enumeration.RuntimeConfigurationEnum.VISIBLE_MOVIES;

@Service
public class DbMovieService {
    public static final long SLEEP_TIME = 5000L;
    private static final Logger LOGGER = LoggerFactory.getLogger(DbMovieService.class);
    private MovieRepository movieRepository;
    private EsMovieService esMovieService;
    private StatisticService statisticService;
    private RuntimeConfigurationService runtimeConfigurationService;

    @Autowired
    public DbMovieService(final MovieRepository movieRepository,
                          final EsMovieService esMovieService,
                          final StatisticService statisticService,
                          final RuntimeConfigurationService runtimeConfigurationService) {
        this.movieRepository = movieRepository;
        this.esMovieService = esMovieService;
        this.statisticService = statisticService;
        this.runtimeConfigurationService = runtimeConfigurationService;
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

    public List<MovieListDto> getTop5RatingMoviesForCaching() {
        return this.getTopNRatingMoviesFromDB().stream()
                .map(this::fillMovieListDtoWithData)
                .collect(Collectors.toList());
    }

    public List<MovieListDto> get5LatestMoviesForCaching() {
        return this.getNLatestMoviesFromDB().stream()
                .map(this::fillMovieListDtoWithData)
                .collect(Collectors.toList());
    }

    public List<Movie> getTopNRatingMoviesFromDB() {
        LOGGER.info("get top N from DB");
        try {
            Thread.sleep(SLEEP_TIME);
        } catch (Exception e) {
            LOGGER.error("sleep error", e);
        }
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findNTopRatedMovies(this.getLimitForVisibleMoviesFromConfig());
    }

    public List<Movie> getNLatestMoviesFromDB() {
        LOGGER.info("get latest N from DB");
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findNLatestMovies(this.getLimitForVisibleMoviesFromConfig());
    }

    public void deleteMovie(final Long movieId) {
        movieRepository.deleteById(movieId);
    }

    private Movie populateMovie(final MovieFormDto dto) {
        return new MovieBuilder()
                .setName(dto.getName())
                .setDescription(dto.getDescription())
                .setDirector(dto.getDirector())
                .setMusic(dto.getMusic())
                .setCamera(dto.getCamera())
                .setForAdults(dto.isForAdults())
                .build();
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

    private int getLimitForVisibleMoviesFromConfig() {
        final VisibleMoviesOptionsRuntimeConfig options = runtimeConfigurationService
                .getRuntimeConfigurationOptions(VISIBLE_MOVIES);
        int limit = options.getLimit();
        LOGGER.info("max visible movies limit is {}", limit);
        return limit;
    }
}

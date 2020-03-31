package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(value = "/movies")
public class MovieCatalogController {
    private DbMovieService dbMovieService;
    private EsMovieService esMovieService;
    private RabbitMqSender rabbitMqSender;
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    @Autowired
    public MovieCatalogController(final DbMovieService dbMovieService,
                                  final EsMovieService esMovieService,
                                  final RabbitMqSender rabbitMqSender) {
        this.dbMovieService = dbMovieService;
        this.esMovieService = esMovieService;
        this.rabbitMqSender = rabbitMqSender;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        LOGGER.info("get all movies request");
        return dbMovieService.getAllMovies();
    }

    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(final @Valid @RequestBody MovieDto dto) {
        LOGGER.info("create movie request");
        Movie movie = null;
        try {
            movie = dbMovieService.createMovie(dto);
        } catch (Exception e) {
            LOGGER.info("Creation of movie failed");
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        rabbitMqSender.sendToElasticQueue(movie.getId().toString());
        rabbitMqSender.sendToLatestMoviesQueue();
        rabbitMqSender.sendToRatingQueue();
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/search")
    public List<Movie> fullTextSearchMovie(final @RequestParam("term") String searchTerm) {
        LOGGER.info("fulltext search request Movie");
        return dbMovieService.fullTextSearch(searchTerm);
    }

    @GetMapping("/EsSearch")
    public List<EsMovie> fullTextSearchEsMovie(final @RequestParam("term") String searchTerm) {
        LOGGER.info("fulltext search request EsMovie");
        return esMovieService.fullTextSearch(searchTerm);
    }

    @GetMapping("/latest5")
    public List<Movie> get5LatestMovies() {
        LOGGER.info("get 5 latest movies request");
        return dbMovieService.get5LatestMoviesFromCache();
    }

    @GetMapping("/top5")
    public List<Movie> getTopRatingMovies() {
        LOGGER.info("get 5 top rating movies request");
        return dbMovieService.getTop5RatingMoviesFromCache();
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        LOGGER.info("get movie detail request");
        return dbMovieService.getMovie(movieId);
    }
}

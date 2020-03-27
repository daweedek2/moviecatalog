package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.MovieService;
import kostka.moviecatalog.service.RabbitMqSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/movies")
public class MovieCatalogController {
    private MovieService<Movie> movieService;
    private MovieService<EsMovie> esMovieService;
    private RabbitMqSender rabbitMqSender;
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    @Autowired
    public MovieCatalogController(final MovieService<Movie> movieService,
                                  final MovieService<EsMovie> esMovieService,
                                  final RabbitMqSender rabbitMqSender) {
        this.movieService = movieService;
        this.esMovieService = esMovieService;
        this.rabbitMqSender = rabbitMqSender;
    }

    @GetMapping("/all")
    public List<Movie> getAllMovies() {
        LOGGER.info("get all movies request");
        return movieService.getAllMovies();
    }

    @GetMapping("/create")
    public Movie createMovie(final @RequestParam("name") String name) {
        LOGGER.info("create movie request");
        Movie movie = null;
        try {
            movie = movieService.createMovie(name);
        } catch (Exception e) {
            LOGGER.info("Creation of movie failed");
            return null;
        }

        rabbitMqSender.sendToElasticQueue(name);
        rabbitMqSender.sendToLatestMoviesQueue(movie.getId().toString());
        rabbitMqSender.sendToRatingQueue();
        return movie;
    }

    @GetMapping("/search")
    public List<Movie> fullTextSearchMovie(final @RequestParam("term") String searchTerm) {
        LOGGER.info("fulltext search request Movie");
        return movieService.fullTextSearch(searchTerm);
    }

    @GetMapping("/EsSearch")
    public List<EsMovie> fullTextSearchEsMovie(final @RequestParam("term") String searchTerm) {
        LOGGER.info("fulltext search request EsMovie");
        return esMovieService.fullTextSearch(searchTerm);
    }

    @GetMapping("/latest5")
    public List<Movie> get5LatestMovies() {
        LOGGER.info("get 5 latest movies request");
        return movieService.get5LatestMovies();
    }

    @GetMapping("/top5")
    public List<Movie> getTopRatingMovies() {
        LOGGER.info("get 5 top rating movies request");
        return movieService.getTop5RatingMoviesFromCache();
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        LOGGER.info("get movie detail request");
        return movieService.getMovie(movieId);
    }
}

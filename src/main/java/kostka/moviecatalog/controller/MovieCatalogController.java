package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.MovieSpecificationService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private MovieSpecificationService specificationService;
    static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogController.class);
    @Autowired
    public MovieCatalogController(final DbMovieService dbMovieService,
                                  final EsMovieService esMovieService,
                                  final RabbitMqSender rabbitMqSender,
                                  final MovieSpecificationService specificationService) {
        this.dbMovieService = dbMovieService;
        this.esMovieService = esMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.specificationService = specificationService;
    }

    @GetMapping("/all")
    public String getAllMovies() {
        LOGGER.info("get all movies request");
        return dbMovieService.getAllMoviesFromCache();
    }

    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(final @Valid @RequestBody MovieDto dto) {
        LOGGER.info("create movie request");
        Movie movie = null;
        try {
            movie = dbMovieService.createMovie(dto);
        } catch (Exception e) {
            LOGGER.error("Creation of movie failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        rabbitMqSender.sendToElasticQueue(movie.getId().toString());
//        rabbitMqSender.sendToLatestMoviesQueue();
//        rabbitMqSender.sendToRatingQueue();
//        rabbitMqSender.sendToAllMoviesQueue();
        rabbitMqSender.sendUpdateRequestToQueue();
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    @GetMapping("/spec")
    public List<Movie> getMoviesBySpec(final @RequestParam("field") String field,
                                       final @RequestParam("operation") String operation,
                                       final @RequestParam("value") String value) {
        LOGGER.info("Search movies by JPA Specification request");
        SearchCriteriaDto dto = new SearchCriteriaDto();
        dto.setField(field);
        dto.setOperation(operation);
        dto.setValue(value);

        return specificationService.getMoviesWithCriteria(dto);
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
    public String get5LatestMovies() {
        LOGGER.info("get 5 latest movies request");
        return dbMovieService.get5LatestMoviesFromCache();
    }

    @GetMapping("/top5")
    public String getTopRatingMovies() {
        LOGGER.info("get 5 top rating movies request");
        return dbMovieService.getTop5RatingMoviesFromCache();
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        LOGGER.info("get movie detail request");
        return dbMovieService.getMovie(movieId);
    }
}

package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.MovieDto;
import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.MovieSpecificationService;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
    private RabbitMqSender rabbitMqSender;
    private MovieSpecificationService specificationService;
    static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogController.class);
    @Autowired
    public MovieCatalogController(final DbMovieService dbMovieService,
                                  final RabbitMqSender rabbitMqSender,
                                  final MovieSpecificationService specificationService) {
        this.dbMovieService = dbMovieService;
        this.rabbitMqSender = rabbitMqSender;
        this.specificationService = specificationService;
    }

    /**
     * Controller method for creating new movie in db and in elasticsearch.
     * @param dto which holds the new movie data.
     * @return response entity with the newly created movie.
     */
    @PostMapping("/create")
    public ResponseEntity<Movie> createMovie(final @Valid @RequestBody MovieDto dto) {
        LOGGER.info("create movie request");
        Movie movie = null;
        try {
            movie = dbMovieService.createMovie(dto);
        } catch (InvalidDtoException e) {
            LOGGER.error("Creation of movie failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        rabbitMqSender.sendToCreateElasticQueue(movie.getId().toString());
        rabbitMqSender.sendUpdateRequestToQueue();
        return new ResponseEntity<>(movie, HttpStatus.OK);
    }

    /**
     * Used for searching movies via JPA specifications.
     * @param field stands for field which is analyzed.
     * @param operation stands for operation which is applied (equals, greater,...).
     * @param value actual value which is search for.
     * @return list of all movies which match the jpa specification query.
     */
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

    /**
     * Method for search for movies in elasticsearch.
     * @param searchTerm string of the desired search term.
     * @return list of all movies which match the search term.
     */
    @GetMapping("/search")
    public List<Movie> fullTextSearchMovie(final @RequestParam("term") String searchTerm) {
        LOGGER.info("fulltext search request Movie");
        return dbMovieService.fullTextSearch(searchTerm);
    }
}

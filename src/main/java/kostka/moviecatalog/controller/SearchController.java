package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.MovieSpecificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/movies")
public class SearchController {
    private static final Logger LOGGER = LoggerFactory.getLogger(SearchController.class);
    private DbMovieService dbMovieService;
    private MovieSpecificationService specificationService;

    @Autowired
    public SearchController(final DbMovieService dbMovieService,
                            final MovieSpecificationService specificationService) {
        this.dbMovieService = dbMovieService;
        this.specificationService = specificationService;
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

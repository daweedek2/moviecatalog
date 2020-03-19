package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.MovieService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;


@RestController
public class MovieCatalogController {
    private MovieService<Movie> movieService;
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    @Autowired
    public MovieCatalogController(final MovieService<Movie> movieService) {
        this.movieService = movieService;
    }

    @GetMapping("movies/all")
    public List<Movie> getAllMovies() {
        LOGGER.info("get all movies request");
        return movieService.getAllMovies();
    }

    @GetMapping("movies/create")
    public Movie createMovie(final @RequestParam("name") String name) {
        LOGGER.info("create movie request");
        return movieService.createMovie(name);
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        LOGGER.info("get movie detail request");
        Optional<Movie> movie = movieService.getMovie(movieId);
        if (movie.isEmpty()) {
            return new Movie();
        }
        return movie.get();
    }
}

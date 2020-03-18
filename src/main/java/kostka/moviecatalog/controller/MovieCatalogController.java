package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.MovieServiceImpl;
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
    private MovieServiceImpl movieServiceImpl;
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    @Autowired
    public MovieCatalogController(final MovieServiceImpl movieServiceImpl) {
        this.movieServiceImpl = movieServiceImpl;
    }

    @GetMapping("movies/all")
    public List<Movie> getAllMovies() {
        LOGGER.info("get all movies request");
        return movieServiceImpl.getAllMovies();
    }

    @GetMapping("movies/create")
    public Movie createMovie(final @RequestParam("name") String name) {
        LOGGER.info("create movie request");
        return movieServiceImpl.createMovie(name);
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        LOGGER.info("get movie detail request");
        Optional<Movie> movie = movieServiceImpl.getMovie(movieId);
        if (movie.isEmpty()) {
            return new Movie();
        }
        return movie.get();
    }
}

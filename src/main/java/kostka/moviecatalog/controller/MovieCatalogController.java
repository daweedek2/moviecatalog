package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class MovieCatalogController {
    private MovieService movieService;

    @Autowired
    public MovieCatalogController(final MovieService movieService) {
        this.movieService = movieService;
    }

    @GetMapping("movies/all")
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }

    @GetMapping("movies/create")
    public Movie createMovie(final @RequestParam("name") String name) {
        return movieService.createMovie(name);
    }

    public Movie getMovieDetail(final @PathVariable("id") Long movieId) {
        Optional<Movie> movie = movieService.getMovie(movieId);
        if (movie.isEmpty()) {
            return new Movie();
        }
        return movie.get();
    }
}

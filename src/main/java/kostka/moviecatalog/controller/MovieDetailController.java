package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.MovieDetail;
import kostka.moviecatalog.service.MovieDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movies")
public class MovieDetailController {
    private final MovieDetailService movieDetailService;

    @Autowired
    public MovieDetailController(final MovieDetailService movieDetailService) {
        this.movieDetailService = movieDetailService;
    }

    /**
     * Method for displaying movie detail page.
     * @param movieId id of the movie.
     * @param model MVC model.
     * @return name of the html file (detail.html).
     */
    @GetMapping("/detail/{movieId}")
    public String getMovieDetail(final @PathVariable Long movieId, final Model model) {
        MovieDetail movieDetail = movieDetailService.getMovieDetail(movieId);
        model.addAttribute("movieDetail", movieDetail);
        return "detail";
    }
}

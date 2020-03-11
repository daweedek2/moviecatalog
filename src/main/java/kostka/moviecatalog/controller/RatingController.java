package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.RatingService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RatingController {
    private RatingService ratingService;

    public RatingController(final RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @GetMapping("/rating/create")
    public Movie createRating(final @RequestParam("id") Long movieId,
                               final @RequestParam("value") int value) {
        return ratingService.createRating(movieId, value);
    }
}

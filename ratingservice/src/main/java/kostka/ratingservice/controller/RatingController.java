package kostka.ratingservice.controller;

import kostka.ratingservice.dto.RatingDto;
import kostka.ratingservice.exception.InvalidDtoException;
import kostka.ratingservice.model.MovieRating;
import kostka.ratingservice.model.Rating;
import kostka.ratingservice.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/rating")
public class RatingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(RatingController.class);
    private RatingService ratingService;

    public RatingController(final RatingService ratingService) {
        this.ratingService = ratingService;
    }

    @PostMapping("/create")
    public Rating createRating(final @Valid @RequestBody RatingDto dto) {
        LOGGER.info("create rating request");
        Rating rating = null;
        try {
            rating = ratingService.createRating(dto);
        } catch (InvalidDtoException e) {
            LOGGER.error("Cannot create rating.", e);
        }
        return rating;
    }

    @GetMapping("/{movieId}")
    public MovieRating getRatingForMovie(final @PathVariable Long movieId) {
        LOGGER.info("get rating for movie with id {}", movieId);
        return new MovieRating(ratingService.getRatingsForMovie(movieId));
    }
}

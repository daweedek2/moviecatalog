package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.RabbitMqSender;
import kostka.moviecatalog.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rating")
public class RatingController {
    static final Logger LOGGER = LoggerFactory.getLogger(RatingController.class);

    private RatingService ratingService;
    private RabbitMqSender rabbitMqSender;

    public RatingController(final RatingService ratingService, final  RabbitMqSender rabbitMqSender) {
        this.ratingService = ratingService;
        this.rabbitMqSender = rabbitMqSender;
    }

    @GetMapping("/create")
    public Movie createRating(final @RequestParam("id") Long movieId,
                               final @RequestParam("value") int value) {
        LOGGER.info("create rating request");
        Movie ratedMovie = ratingService.createRating(movieId, value);
        rabbitMqSender.sendToRatingQueue();
        return ratedMovie;
    }
}

package kostka.moviecatalog.controller;

import kostka.moviecatalog.dto.RatingDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import kostka.moviecatalog.service.RatingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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

    @PostMapping("/create")
    public ResponseEntity<Movie> createRating(final @Valid @RequestBody RatingDto dto) {
        LOGGER.info("create rating request");
        Movie ratedMovie = null;
        try {
            ratedMovie = ratingService.createRating(dto);
        } catch (Exception e) {
            LOGGER.error("Rating creation failed", e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        rabbitMqSender.sendToRatingQueue();
        rabbitMqSender.sendToAllMoviesQueue();
        rabbitMqSender.sendToLatestMoviesQueue();
        return new ResponseEntity<>(ratedMovie, HttpStatus.OK);
    }
}

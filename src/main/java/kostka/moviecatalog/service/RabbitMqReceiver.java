package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqReceiver {
    public static final String TOPIC_EXCHANGE = "movie-exchange";
    public static final String ELASTIC_QUEUE = "elastic-queue";
    public static final String LATEST_MOVIES_QUEUE = "latest-movies-queue";
    public static final String RATING_QUEUE = "rating-queue";
    public static final String DEFAULT_QUEUE = "default-queue";
    public static final String CREATE_MOVIE_KEY = "createMovie";
    public static final String LATEST_MOVIES_KEY = "latestMovies";
    public static final String TOP_RATING_KEY = "rating";
    public static final String DEFAULT_KEY = "default";

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    private MovieService<EsMovie> esMovieService;
    private MovieService<Movie> movieService;
    private RedisService redisService;

    @Autowired
    public RabbitMqReceiver(final MovieService<EsMovie> esMovieService,
                            final MovieService<Movie> movieService,
                            final RedisService redisService) {
        this.esMovieService = esMovieService;
        this.movieService = movieService;
        this.redisService = redisService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = CREATE_MOVIE_KEY,
                    value = @Queue(ELASTIC_QUEUE)
            )
    )
    public void receiveMessageElasticQueue(final String movieName) {
        LOGGER.info("Received movie from rabbitMQ elastic-queue with name '{}'.", movieName);
        esMovieService.createMovie(movieName);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = LATEST_MOVIES_KEY,
                    value = @Queue(LATEST_MOVIES_QUEUE)
            )
    )
    public void receiveMessageLatestMoviesQueue(final String movieId) {
        LOGGER.info("Received movieId from rabbitMQ latest-movies queue: '{}'.", movieId);
        redisService.updateLatestMovie(movieId);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = TOP_RATING_KEY,
                    value = @Queue(RATING_QUEUE)
            )
    )
    public void receiveMessageRatingQueue() {
        LOGGER.info("Received message from RabbitMQ rating-queue to recalculate TOP5 movies by rating");
        redisService.updateTopRatingMovies(movieService.getTop5RatingMoviesFromDB());
    }
}

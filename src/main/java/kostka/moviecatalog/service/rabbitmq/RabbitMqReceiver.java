package kostka.moviecatalog.service.rabbitmq;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.STOMPService;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RabbitMqReceiver {
    public static final String TOPIC_EXCHANGE = "movie-exchange";
    public static final String ELASTIC_QUEUE = "elastic-queue";
    public static final String LATEST_MOVIES_QUEUE = "latest-movies-queue";
    public static final String ALL_MOVIES_QUEUE = "all-movies-queue";
    public static final String RATING_QUEUE = "rating-queue";
    public static final String DEFAULT_QUEUE = "default-queue";
    public static final String CREATE_MOVIE_KEY = "createMovie";
    public static final String LATEST_MOVIES_KEY = "latestMovies";
    public static final String TOP_RATING_KEY = "rating";
    public static final String ALL_MOVIES_KEY = "all-movies";
    public static final String DEFAULT_KEY = "default";
    public static final String CANNOT_PARSE_JSON = "Cannot parse JSON";

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqReceiver.class);

    private EsMovieService esMovieService;
    private DbMovieService dbMovieService;
    private RedisService redisService;
    private STOMPService stompService;

    @Autowired
    public RabbitMqReceiver(final EsMovieService esMovieService,
                            final DbMovieService dbmovieService,
                            final RedisService redisService,
                            final STOMPService stompService) {
        this.esMovieService = esMovieService;
        this.dbMovieService = dbmovieService;
        this.redisService = redisService;
        this.stompService = stompService;
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = CREATE_MOVIE_KEY,
                    value = @Queue(ELASTIC_QUEUE)
            )
    )
    public void receiveMessageElasticQueue(final String id) {
        LOGGER.info("Received movie from rabbitMQ elastic-queue with id '{}'.", id);
        esMovieService.createMovie(id);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = LATEST_MOVIES_KEY,
                    value = @Queue(LATEST_MOVIES_QUEUE)
            )
    )
    public void receiveMessageLatestMoviesQueue() {
        LOGGER.info("Received message from RabbitMQ latest-movies-queue to recalculate latest movies.");
        List<Movie> latestMovies = dbMovieService.get5LatestMoviesFromDB();
        if (latestMovies.isEmpty()) {
            return;
        }
        try {
            redisService.updateMoviesInRedis(latestMovies, LATEST_MOVIES_KEY);
        } catch (JsonProcessingException e) {
            LOGGER.error(CANNOT_PARSE_JSON, e);
            return;
        }
        stompService.sendSTOMPToUpdateLatestMovies();
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
        List<Movie> topMovies = dbMovieService.getTop5RatingMoviesFromDB();
        if (topMovies.isEmpty()) {
            return;
        }
        try {
            redisService.updateMoviesInRedis(topMovies, TOP_RATING_KEY);
        } catch (JsonProcessingException e) {
            LOGGER.error(CANNOT_PARSE_JSON, e);
            return;
        }
        stompService.sendSTOMPToUpdateTopRatedMovies();
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = ALL_MOVIES_KEY,
                    value = @Queue(ALL_MOVIES_QUEUE)
            )
    )
    public void receiveMessageAllMoviesQueue() {
        LOGGER.info("Received message from RabbitMQ all-movies-queue to recalculate all movies");
        List<Movie> allMovies = dbMovieService.getAllMoviesFromDB();
        if (allMovies.isEmpty()) {
            return;
        }
        try {
            redisService.updateMoviesInRedis(allMovies, ALL_MOVIES_KEY);
        } catch (JsonProcessingException e) {
            LOGGER.error(CANNOT_PARSE_JSON, e);
            return;
        }
        stompService.sendSTOMPToUpdateAllMovies();
    }
}

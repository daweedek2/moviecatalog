package kostka.moviecatalog.service.rabbitmq;

import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.redis.RedisService;
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

    private EsMovieService esMovieService;
    private DbMovieService dbMovieService;
    private RedisService redisService;

    @Autowired
    public RabbitMqReceiver(final EsMovieService esMovieService,
                            final DbMovieService dbmovieService,
                            final RedisService redisService) {
        this.esMovieService = esMovieService;
        this.dbMovieService = dbmovieService;
        this.redisService = redisService;
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
        redisService.updateLatestMovies(dbMovieService.get5LatestMoviesFromDB());
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
        redisService.updateTopRatingMovies(dbMovieService.getTop5RatingMoviesFromDB());
    }
}

package kostka.moviecatalog.service.rabbitmq;

import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.DbMovieService;
import kostka.moviecatalog.service.EsMovieService;
import kostka.moviecatalog.service.ExternalRatingService;
import kostka.moviecatalog.service.STOMPService;
import kostka.moviecatalog.service.StatisticService;
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
import java.util.concurrent.CompletableFuture;

@Component
public class RabbitMqReceiver {
    public static final String TOPIC_EXCHANGE = "movie-exchange";
    public static final String CREATE_ELASTIC_QUEUE = "create-elastic-queue";
    public static final String DELETE_ELASTIC_QUEUE = "delete-elastic-queue";
    public static final String LATEST_MOVIES_QUEUE = "latest-movies-queue";
    public static final String ALL_MOVIES_QUEUE = "all-movies-queue";
    public static final String RATING_QUEUE = "rating-queue";
    public static final String RECALCULATE_QUEUE = "recalculate-queue";
    public static final String DEFAULT_QUEUE = "default-queue";
    public static final String CREATE_MOVIE_KEY = "createMovie";
    public static final String DELETE_MOVIE_KEY = "deleteMovie";
    public static final String LATEST_MOVIES_KEY = "latestMovies";
    public static final String TOP_RATING_KEY = "rating";
    public static final String ALL_MOVIES_KEY = "all-movies";
    public static final String RECALCULATE_KEY = "recalculate";
    public static final String RATING_KEY = "rating";
    public static final String MOVIE_DETAIL_KEY = "movieDetail";
    public static final String DEFAULT_KEY = "default";
    public static final String CANNOT_PARSE_JSON = "Cannot parse JSON";
    public static final String TOPIC = "/topic/";

    private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqReceiver.class);

    private EsMovieService esMovieService;
    private DbMovieService dbMovieService;
    private RedisService redisService;
    private STOMPService stompService;
    private StatisticService statisticService;
    private ExternalRatingService externalRatingService;

    @Autowired
    public RabbitMqReceiver(final EsMovieService esMovieService,
                            final DbMovieService dbmovieService,
                            final RedisService redisService,
                            final STOMPService stompService,
                            final StatisticService statisticService,
                            final ExternalRatingService externalRatingService) {
        this.esMovieService = esMovieService;
        this.dbMovieService = dbmovieService;
        this.redisService = redisService;
        this.stompService = stompService;
        this.statisticService = statisticService;
        this.externalRatingService = externalRatingService;
    }

    /**
     * Method which takes care of getting message from RabbitMQ elastic-queue for creating movie in elasticsearch.
     * @param id id of the movie which will be created in elasticsearch.
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = CREATE_MOVIE_KEY,
                    value = @Queue(CREATE_ELASTIC_QUEUE)
            )
    )
    public void receiveMessageCreateKeyElasticQueue(final String id) {
        LOGGER.info("Received movie from rabbitMQ elastic-queue with id '{}'.", id);
        statisticService.incrementSyncedRabbitMqCounter();
        try {
            esMovieService.createMovie(id);
        } catch (Exception e) {
            LOGGER.error("Movie cannot be created in ES.", e);
        }
    }

    /**
     * Method which takes care of getting message from RabbitMQ elastic-queue for deleting movie from elasticsearch.
     * @param id id of the movie which will be deleted from elasticsearch.
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = DELETE_MOVIE_KEY,
                    value = @Queue(DELETE_ELASTIC_QUEUE)
            )
    )
    public void receiveMessageDeleteKeyElasticQueue(final String id) {
        LOGGER.info("Received movie from rabbitMQ elastic-queue with id '{}'.", id);
        statisticService.incrementSyncedRabbitMqCounter();
        try {
            esMovieService.deleteEsMovie(id);
        } catch (Exception e) {
            LOGGER.error("Movie with id '{}' cannot be deleted from ES.", id, e);
        }
    }

    /**
     * Method which takes care of getting message from RabbitMQ recalculate-queue for updating
     * all tables in Redis cache and then inform FE via STOMP message.
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = RECALCULATE_KEY,
                    value = @Queue(RECALCULATE_QUEUE)
            )
    )
    public void receiveUpdateRequestRecalculateQueue() {
        LOGGER.info("Received message from RabbitMQ to recalculate all tables.");
        statisticService.incrementSyncedRabbitMqCounter();
        try {
            CompletableFuture<Void> allMoviesRedis = redisService
                    .tryToUpdateMoviesInRedis(dbMovieService::getAllMoviesFromDB, ALL_MOVIES_KEY);
            CompletableFuture<Void> topMoviesRedis = redisService
                    .tryToUpdateMoviesInRedis(dbMovieService::getTop5RatingMoviesFromDB, TOP_RATING_KEY);
            CompletableFuture<Void> latestMoviesRedis = redisService
                    .tryToUpdateMoviesInRedis(dbMovieService::get5LatestMoviesFromDB, LATEST_MOVIES_KEY);

            CompletableFuture.allOf(allMoviesRedis, topMoviesRedis, latestMoviesRedis).join();
            stompService.sendSTOMPToUpdateAllTables();
        } catch (Exception e) {
            LOGGER.error("Completable future error?", e);
        }
    }

    /**
     * Method which takes care of getting message from RabbitMQ rating-queue for updating
     * the average rating of all movies in db.
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = RATING_KEY,
                    value = @Queue(RATING_QUEUE)
            )
    )
    public void receiveRecalculateAverageMovieRating() {
        LOGGER.info("Received message from RabbitMQ to get Ratings for all movies.");
        statisticService.incrementSyncedRabbitMqCounter();
        List<Movie> allMovies = dbMovieService.getAllMoviesFromDB();
        allMovies.forEach(
                movie -> {
                    LOGGER.info("Updating movie with id {} average rating.", movie.getId());
                    movie.setAverageRating(
                            externalRatingService.getAverageRatingFromRatingService(movie.getId())
                    );
                    dbMovieService.saveMovie(movie);
                }
                );
        stompService.sendSTOMPToRefreshMovieDetail();
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = RATING_KEY,
                    value = @Queue(RATING_QUEUE)
            )
    )
    public void receiveRefreshMovieDetailRequest() {
        LOGGER.info("Received message from RabbitMQ to refresh movie detail.");
        statisticService.incrementSyncedRabbitMqCounter();
        stompService.sendSTOMPToRefreshMovieDetail();
    }
}

package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
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
    public static final String DEFAULT_QUEUE = "default-queue";
    public static final String CREATE_MOVIE_KEY = "createMovie";
    public static final String DEFAULT_KEY = "default";

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    private MovieService<EsMovie> movieService;

    @Autowired
    public RabbitMqReceiver(final MovieService<EsMovie> movieService) {
        this.movieService = movieService;
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
        movieService.createMovie(movieName);
    }

    @RabbitListener(
            bindings = @QueueBinding(
                    exchange = @Exchange(TOPIC_EXCHANGE),
                    key = DEFAULT_KEY,
                    value = @Queue(DEFAULT_QUEUE)
            )
    )
    public void receiveMessageDefaultQueue(final String message) {
        LOGGER.info("Received message from rabbitMQ default queue: '{}'.", message);
    }
}

package kostka.moviecatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.RabbitMqReceiver.TOPIC_EXCHANGE;

@Service
public class RabbitMqSender {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqSender(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendToElasticQueue(final String movieName) {
        LOGGER.info("Starting sending of movie name '{}' to rabbitMQ elastic-queue.", movieName);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, CREATE_MOVIE_KEY, movieName);
        LOGGER.info("Movie name '{}' is sent to rabbitMQ elastic-queue.", movieName);
    }

    public void sendToLatestMoviesQueue(final String message) {
        LOGGER.info("Starting sending of movieId '{}' to rabbitMQ latest-movies-queue.", message);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, LATEST_MOVIES_KEY, message);
        LOGGER.info("MovieId '{}' is sent to rabbitMQ latest-movies-queue.", message);
    }
}

package kostka.moviecatalog.service.rabbitmq;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC_EXCHANGE;

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

    public void sendToLatestMoviesQueue() {
        LOGGER.info("Starting sending recalculate latest movies request to rabbitMQ latest-movies-queue.");
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, LATEST_MOVIES_KEY, LATEST_MOVIES_KEY);
        LOGGER.info("Recalculate latest movies request sent to rabbitMQ latest-movies-queue.");
    }

    public void sendToRatingQueue() {
        LOGGER.info("Starting sending recalculate top 5 movies request to rabbitMQ rating-queue.");
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, TOP_RATING_KEY, TOP_RATING_KEY);
        LOGGER.info("Recalculate top 5 movies request is sent to rabbitMQ rating-queue.");
    }
}

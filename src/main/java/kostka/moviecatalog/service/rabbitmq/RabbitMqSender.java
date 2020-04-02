package kostka.moviecatalog.service.rabbitmq;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC_EXCHANGE;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Service
public class RabbitMqSender {
    static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSender.class);
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

    public void sendToAllMoviesQueue() {
        LOGGER.info("Starting sending recalculate all movies request to rabbitMQ all-movies-queue.");
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, ALL_MOVIES_KEY, ALL_MOVIES_KEY);
        LOGGER.info("Recalculate all movies request sent to rabbitMQ all-movies-queue.");
    }
}

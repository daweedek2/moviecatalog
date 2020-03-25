package kostka.moviecatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.RabbitMqReceiver.DEFAULT_KEY;
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
        LOGGER.info("Starting sending of movie with name '{}' to rabbitMQ elastic-queue.", movieName);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, CREATE_MOVIE_KEY, movieName);
        LOGGER.info("Movie with name '{}' is sent to rabbitMQ elastic-queue.", movieName);
    }

    public void sendToDefaultQueue(final String message) {
        LOGGER.info("Starting sending of message '{}' to rabbitMQ default queue.", message);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, DEFAULT_KEY, message);
        LOGGER.info("Message '{}' is sent to rabbitMQ default queue.", message);
    }
}

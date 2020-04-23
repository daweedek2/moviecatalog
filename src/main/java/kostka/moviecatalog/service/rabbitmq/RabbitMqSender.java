package kostka.moviecatalog.service.rabbitmq;

import kostka.moviecatalog.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.DELETE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RATING_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RECALCULATE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC_EXCHANGE;

@Service
public class RabbitMqSender {
    static final Logger LOGGER = LoggerFactory.getLogger(RabbitMqSender.class);
    private RabbitTemplate rabbitTemplate;
    private StatisticService statisticService;

    @Autowired
    public RabbitMqSender(final RabbitTemplate rabbitTemplate, final StatisticService statisticService) {
        this.rabbitTemplate = rabbitTemplate;
        this.statisticService = statisticService;
    }

    /**
     * Sends message to rabbitMQ which then creates movie in ES.
     * @param movieName name of the movie.
     */
    public void sendToCreateElasticQueue(final String movieName) {
        LOGGER.info("Starting sending of movie name '{}' to rabbitMQ create elastic-queue.", movieName);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, CREATE_MOVIE_KEY, movieName);
        statisticService.incrementSyncedRabbitMqCounter();
        LOGGER.info("Movie name '{}' is sent to rabbitMQ create elastic-queue.", movieName);
    }

    /**
     * Sends message to rabbitMQ which then deletes movie in ES.
     * @param movieName name of the movie.
     */
    public void sendToDeleteElasticQueue(final String movieName) {
        LOGGER.info("Starting sending of movie name '{}' to rabbitMQ delete elastic-queue.", movieName);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, DELETE_MOVIE_KEY, movieName);
        statisticService.incrementSyncedRabbitMqCounter();
        LOGGER.info("Movie name '{}' is sent to rabbitMQ delete elastic-queue.", movieName);
    }

    /**
     * Sends message to rabbitMQ which then updates all data in Redis.
     */
    public void sendUpdateRequestToQueue() {
        LOGGER.info("Sending recalculate all tables request to rabbitMQ recalculate-queue.");
        statisticService.incrementSyncedRabbitMqCounter();
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, RECALCULATE_KEY, RECALCULATE_KEY);
    }

    /**
     * Sends message to rabbitMQ which then updates all average ratings for all movies.
     */
    public void sendToGetAverageRatingForAllMovies() {
        LOGGER.info("Sending recalculate all movies average rating request to rabbitMQ rating-queue.");
        statisticService.incrementSyncedRabbitMqCounter();
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, RATING_KEY, RATING_KEY);
    }
}

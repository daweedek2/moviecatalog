package kostka.moviecatalog.service.rabbitmq;

import kostka.moviecatalog.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CREATE_MOVIE_KEY;
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

    public void sendToElasticQueue(final String movieName) {
        LOGGER.info("Starting sending of movie name '{}' to rabbitMQ elastic-queue.", movieName);
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, CREATE_MOVIE_KEY, movieName);
        statisticService.incrementSyncedRabbitMqCounter();
        LOGGER.info("Movie name '{}' is sent to rabbitMQ elastic-queue.", movieName);
    }

    public void sendUpdateRequestToQueue() {
        LOGGER.info("Sending recalculate all tables request to rabbitMQ recalculate-queue.");
        statisticService.incrementSyncedRabbitMqCounter();
        rabbitTemplate.convertAndSend(TOPIC_EXCHANGE, RECALCULATE_KEY, RECALCULATE_KEY);
    }
}

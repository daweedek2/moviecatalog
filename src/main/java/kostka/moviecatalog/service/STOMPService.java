package kostka.moviecatalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RECALCULATE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;


@Service
public class STOMPService {
    private SimpMessagingTemplate messagingTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(STOMPService.class);

    public STOMPService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendSTOMPToUpdateLatestMovies() {
        LOGGER.info("Sending STOMP to refresh latest movies table");
        messagingTemplate.convertAndSend(TOPIC + LATEST_MOVIES_KEY, LATEST_MOVIES_KEY);
    }

    public void sendSTOMPToUpdateTopRatedMovies() {
        LOGGER.info("Sending STOMP to refresh topRated movies table");
        messagingTemplate.convertAndSend(TOPIC + TOP_RATING_KEY, TOP_RATING_KEY);
    }

    public void sendSTOMPToUpdateAllMovies() {
        LOGGER.info("Sending STOMP to refresh topRated movies table");
        messagingTemplate.convertAndSend(TOPIC + ALL_MOVIES_KEY, ALL_MOVIES_KEY);
    }

    public void sendSTOMPToUpdateAllTables() {
        LOGGER.info("Sending STOMP to refresh all tables");
        messagingTemplate.convertAndSend(TOPIC + RECALCULATE_KEY, RECALCULATE_KEY);
    }
}

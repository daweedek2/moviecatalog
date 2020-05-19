package kostka.moviecatalog.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ADMIN_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.MOVIE_DETAIL_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RECALCULATE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC;

/**
 * Service which sends STOMP messages to the FE.
 */
@Service
public class STOMPService {
    private SimpMessagingTemplate messagingTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(STOMPService.class);

    public STOMPService(final SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void sendSTOMPToUpdateAllTables() {
        LOGGER.info("Sending STOMP to refresh all tables");
        messagingTemplate.convertAndSend(TOPIC + RECALCULATE_KEY, RECALCULATE_KEY);
    }

    public void sendSTOMPToRefreshMovieDetail() {
        LOGGER.info("Sending STOMP to refresh movie detail page");
        messagingTemplate.convertAndSend(TOPIC + MOVIE_DETAIL_KEY, MOVIE_DETAIL_KEY);
    }

    public void sendSTOMPToRefreshAdmin() {
        LOGGER.info("Sending STOMP to refresh admin page.");
        messagingTemplate.convertAndSend(TOPIC + ADMIN_KEY, ADMIN_KEY);
    }
}

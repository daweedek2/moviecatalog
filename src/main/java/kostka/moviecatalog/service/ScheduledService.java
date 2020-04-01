package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    private static final int INIT_DELAY = 10000;
    private static final int FIXED_DELAY = 60000;
    private RabbitMqSender rabbitMqSender;

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    public ScheduledService(final RabbitMqSender rabbitMqSender) {
        this.rabbitMqSender = rabbitMqSender;
    }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshLatestMovies() {
        LOGGER.info("[Scheduled task] Refreshing latest movies.");
        rabbitMqSender.sendToLatestMoviesQueue();
        }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshTopRatedMovies() {
        LOGGER.info("[Scheduled task] Refreshing top rating movies.");
        rabbitMqSender.sendToRatingQueue();
    }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshAllMovies() {
        LOGGER.info("[Scheduled task] Refreshing latest movies.");
        rabbitMqSender.sendToAllMoviesQueue();
    }
}

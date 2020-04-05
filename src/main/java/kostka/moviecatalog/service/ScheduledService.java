package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    private static final int INIT_DELAY = 10000;
    private static final int FIXED_DELAY = 60000;
    public static final String SCHEDULED_TASK_NO_MOVIE_IN_DB = "[Scheduled task] NO Movie in DB.";
    private RabbitMqSender rabbitMqSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);

    public ScheduledService(final RabbitMqSender rabbitMqSender) {
        this.rabbitMqSender = rabbitMqSender;
    }
/*
    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshLatestMovies() {
        LOGGER.info("[Scheduled task] Refreshing latest movies.");
        try {
            rabbitMqSender.sendToLatestMoviesQueue();
        } catch (Exception e) {
            LOGGER.error(SCHEDULED_TASK_NO_MOVIE_IN_DB, e);
        }
        }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshTopRatedMovies() {
        LOGGER.info("[Scheduled task] Refreshing top rating movies.");
        try {
            rabbitMqSender.sendToRatingQueue();
        } catch (Exception e) {
            LOGGER.error(SCHEDULED_TASK_NO_MOVIE_IN_DB, e);
        }
    }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshAllMovies() {
        LOGGER.info("[Scheduled task] Refreshing latest movies.");
        try {
            rabbitMqSender.sendToAllMoviesQueue();
        } catch (Exception e) {
            LOGGER.error(SCHEDULED_TASK_NO_MOVIE_IN_DB, e);
        }
    }*/
}

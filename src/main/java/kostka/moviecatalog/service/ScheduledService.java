package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

/**
 * Service which is used for scheduling tasks.
 */
@Service
public class ScheduledService {
    private static final int INIT_DELAY_RATING = 5000;
    private static final int INIT_DELAY_FE = 10000;
    private static final int FIXED_DELAY = 60000;
    public static final String SCHEDULED_TASK_NO_MOVIE_IN_DB = "[Scheduled task] NO Movie in DB.";
    private RabbitMqSender rabbitMqSender;
    private StatisticService statisticService;

    private static final Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);

    public ScheduledService(final RabbitMqSender rabbitMqSender, final StatisticService statisticService) {
        this.rabbitMqSender = rabbitMqSender;
        this.statisticService = statisticService;
    }

    /**
     * This method is executed every configured time in the Scheduled annotation and it updates all movies
     * in Redis cache and then informs FE with Stomp message.
     */
//    @Scheduled(initialDelayString = "${scheduled.init.delay}", fixedDelayString = "${scheduled.fixed.delay}")
    @Scheduled(initialDelay = INIT_DELAY_FE, fixedDelay = FIXED_DELAY)
    public void updateAllMoviesInRedisAndFE() {
        LOGGER.info("[Scheduled task] Refreshing all movies stored in REDIS and then inform FE via stomp.");
        statisticService.incrementSyncedScheduledCounter();
        try {
            rabbitMqSender.sendUpdateRequestToQueue();
        } catch (Exception e) {
            LOGGER.error(SCHEDULED_TASK_NO_MOVIE_IN_DB, e);
        }
    }

    /**
     * This method is executed every configured time in the Scheduled annotation and it updates all average ratings
     * of all movies in db by value from external microservice Rating Service.
     */
    @Scheduled(initialDelay = INIT_DELAY_RATING, fixedDelay = FIXED_DELAY)
    public void updateAllMoviesAverageRatingInDb() {
        LOGGER.info("[Scheduled task] Updating all movies average rating in DB.");
        statisticService.incrementSyncedScheduledCounter();
        try {
            rabbitMqSender.sendToGetAverageRatingForAllMovies();
        } catch (Exception e) {
            LOGGER.error(SCHEDULED_TASK_NO_MOVIE_IN_DB, e);
        }
    }
}

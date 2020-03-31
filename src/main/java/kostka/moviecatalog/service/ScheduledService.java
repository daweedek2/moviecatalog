package kostka.moviecatalog.service;

import kostka.moviecatalog.service.redis.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class ScheduledService {
    private static final int INIT_DELAY = 10000;
    private static final int FIXED_DELAY = 60000;
    private RedisService redisService;
    private DbMovieService dbMovieService;

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    public ScheduledService(final RedisService redisService, final DbMovieService dbMovieService) {
        this.redisService = redisService;
        this.dbMovieService = dbMovieService;
    }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshLatestMovies() {
        LOGGER.info("[Scheduled task] Refreshing latest movies.");
        try {
            redisService.updateLatestMovies(dbMovieService.get5LatestMoviesFromDB());
        } catch (Exception e) {
            LOGGER.info("[Scheduled task] No movie in DB");
        }
    }

    @Scheduled(initialDelay = INIT_DELAY, fixedDelay = FIXED_DELAY)
    public void refreshTopRatedMovies() {
        LOGGER.info("[Scheduled task] Refreshing top rating movies.");
        try {
            redisService.updateTopRatingMovies(dbMovieService.getTop5RatingMoviesFromDB());
        } catch (Exception e) {
            LOGGER.info("[Scheduled task] No movie in DB");
        }
    }
}

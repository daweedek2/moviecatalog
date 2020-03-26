package kostka.moviecatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {
    static final String LATEST_MOVIES_KEY = "latestMovies";
    private static final Long N_LATEST_MOVIES = 5L;

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateLatestMovie(final String movieId) {
        LOGGER.info("Adding movieId '{}' to the redis cache", movieId);
        while (redisTemplate.opsForList().size(LATEST_MOVIES_KEY) >= N_LATEST_MOVIES) {
            String removedId = redisTemplate.opsForList().rightPop(LATEST_MOVIES_KEY);
            LOGGER.info("Removing movieId '{}' from Redis cache of top 5 latest movies", removedId);
        }
        redisTemplate.opsForList().leftPush(LATEST_MOVIES_KEY, movieId);
        LOGGER.info("MovieId '{}' is added to the redis cache", movieId);
        LOGGER.info("Current list of latest movies: {}",
                redisTemplate.opsForList().range(LATEST_MOVIES_KEY, 0L, -1L));
    }

    public List<String> getLatestMovieIds() {
        LOGGER.info("Getting top 5 latest movies from Redis cache.");
        return redisTemplate.opsForList().range(LATEST_MOVIES_KEY, 0L, -1L);
    }
}

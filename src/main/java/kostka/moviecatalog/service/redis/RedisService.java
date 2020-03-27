package kostka.moviecatalog.service.redis;

import kostka.moviecatalog.entity.Movie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;

@Service
public class RedisService {
    public static final long START = 0L;
    public static final long LIMIT = 4L;
    public static final long END = -1L;

    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateLatestMovie(final String movieId) {
        LOGGER.info("Adding movieId '{}' to the latest movies redis cache", movieId);
        redisTemplate.opsForList().leftPush(LATEST_MOVIES_KEY, movieId);
        redisTemplate.opsForList().trim(LATEST_MOVIES_KEY, START, LIMIT);
        LOGGER.info("MovieId '{}' is added to the latest movies redis cache", movieId);
        LOGGER.info("Current list of top 5 latest movies: '{}'",
                redisTemplate.opsForList().range(LATEST_MOVIES_KEY, START, END));
    }

    public void updateTopRatingMovies(final List<Movie> movies) {
        List<String> ids = movies.stream()
                .map(Movie::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        LOGGER.info("Adding movie Ids '{}' to the top rating redis cache", ids);
        redisTemplate.opsForList().trim(TOP_RATING_KEY, END, START);
        redisTemplate.opsForList().rightPushAll(TOP_RATING_KEY, ids);
        LOGGER.info("Ids '{}' are added to the top rating redis cache", ids);
        LOGGER.info("Current list of top 5 rating movies: '{}'",
                redisTemplate.opsForList().range(TOP_RATING_KEY, START, END));
    }

    public List<String> getListFromCacheWithKey(final String key) {
        LOGGER.info("Getting movie Ids from Redis cache with key '{}'.", key);
        return redisTemplate.opsForList().range(key, START, END);
    }
}

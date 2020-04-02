package kostka.moviecatalog.service.redis;

import kostka.moviecatalog.entity.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateLatestMovies(final List<Movie> movies) {
        List<String> ids = movies.stream()
                .map(Movie::getId)
                .map(String::valueOf)
                .collect(Collectors.toList());

        LOGGER.info("Adding movie Ids '{}' to the latest movies redis cache", ids);
        redisTemplate.opsForList().trim(LATEST_MOVIES_KEY, END, START);
        redisTemplate.opsForList().rightPushAll(LATEST_MOVIES_KEY, ids);
        LOGGER.info("Ids '{}' are added to the latest movies redis cache", ids);
        LOGGER.info("Current list of 5 latest movies movies: '{}'",
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

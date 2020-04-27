package kostka.moviecatalog.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CANNOT_PARSE_JSON;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static final String GENERAL_COUNTER = "general-counter";

    private RedisTemplate<String, String> redisTemplate;
    private StatisticService statisticService;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate, final StatisticService statisticService) {
        this.redisTemplate = redisTemplate;
        this.statisticService = statisticService;
    }

    /**
     * Updates list of movies in Redis cache.
     * @param movies list of movies which needs to be updated.
     * @param key Redis key which is used to map the proper list of movies.
     * @throws JsonProcessingException
     */
    public void updateMoviesInRedis(final List<Movie> movies, final String key) throws JsonProcessingException {
        String json = getJsonStringFromList(movies);
        LOGGER.info("Adding movies '{}' to the redis cache with key: '{}'", json, key);
        redisTemplate.opsForValue().set(key, json);
        statisticService.incrementSyncedRedisCounter();
    }

    /**
     * Gets data from the Redis cache with the Redis key.
     * @param key key of the data in Redis cache.
     * @return data from Redis cache.
     */
    public String getMoviesWithKey(final String key) {
        LOGGER.info("Getting movies from Redis cache with key '{}'.", key);
        statisticService.incrementSyncedRedisCounter();
        return redisTemplate.opsForValue().get(key);
    }

    private String getJsonStringFromList(final List<Movie> movies) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(movies);
    }

    /**
     * Waits until data are loaded from DB and then updated in Redis.
     * @param moviesSupplier method for fetching movies from db.
     * @param key key of the data in Redis cache.
     * @return
     */
    @Async
    public CompletableFuture<Void> tryToUpdateMoviesInRedis(
            final Supplier<List<Movie>> moviesSupplier,
            final String key) {
        List<Movie> movies = moviesSupplier.get();
        if (movies.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        }
        try {
            this.updateMoviesInRedis(movies, key);
            return CompletableFuture.completedFuture(null);
        } catch (JsonProcessingException e) {
            LOGGER.error(CANNOT_PARSE_JSON, e);
            return CompletableFuture.failedFuture(e);
        }
    }

    public void incrementGeneralCounter() {
        int oldValue;
        int newValue;
        String stringValue = redisTemplate.opsForValue().get(GENERAL_COUNTER);
        if (stringValue == null) {
            //if the counter is null, initialize it with 0
            LOGGER.info("Initialize general counter with value 0.");
            oldValue = 0;
        } else {
            oldValue = Integer.parseInt(stringValue);
        }

        newValue = oldValue + 1;
        redisTemplate.opsForValue().set(GENERAL_COUNTER, String.valueOf(newValue));
        LOGGER.info("MovieCatalog - General counter is incremented from '{}' to '{}'.", oldValue, newValue);
    }
}

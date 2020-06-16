package kostka.moviecatalog.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import kostka.moviecatalog.configuration.RedisLockConfiguration;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.service.JsonConvertService;
import kostka.moviecatalog.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CANNOT_PARSE_JSON;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    public static final String GENERAL_COUNTER = "general-counter";

    private RedisTemplate<String, String> redisTemplate;
    private StatisticService statisticService;
    private JsonConvertService jsonConvertService;

    private Jedis jedis = new Jedis("localhost", 6379);
    private RedisLockConfiguration redisLock = new RedisLockConfiguration(jedis, "lock", 8000, 3000);

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate,
                        final StatisticService statisticService,
                        final JsonConvertService jsonConvertService) {
        this.redisTemplate = redisTemplate;
        this.statisticService = statisticService;
        this.jsonConvertService = jsonConvertService;
    }

    public void saveDataToRedisCache(final String key, final String jsonData) {
        LOGGER.info("Adding data '{}' to the redis cache with key: '{}'", jsonData, key);
        redisTemplate.opsForValue().set(key, jsonData);
        statisticService.incrementSyncedRedisCounter();
    }

    public String getDataFromRedisCache(final String key) {
        LOGGER.info("Getting data from redis cache with key: '{}'", key);
        statisticService.incrementSyncedRedisCounter();
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * Updates list of movies in Redis cache.
     * @param movies list of movies which needs to be updated.
     * @param key Redis key which is used to map the proper list of movies.
     * @throws JsonProcessingException
     */
    public void updateMoviesInRedis(final List<MovieListDto> movies, final String key) throws JsonProcessingException {
        String json = jsonConvertService.dataToJson(movies);
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

    /**
     * Waits until data are loaded from DB and then updated in Redis.
     * @param moviesSupplier method for fetching movies from db.
     * @param key key of the data in Redis cache.
     * @return
     */
    @Async
    public CompletableFuture<Void> tryToUpdateMoviesInRedis(
            final Supplier<List<MovieListDto>> moviesSupplier,
            final String key) {
        List<MovieListDto> movies = moviesSupplier.get();
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

    /**
     * Increments general counter, which holds number of created objects (movie, rating, comment)
     * General counter is secured by distributed lock.
     */
    public void incrementGeneralCounterWithLockCheck() {
        try {
            if (redisLock.lock()) {
                LOGGER.info("Got lock and incrementing general counter");
                this.incrementGeneralCounterInRedis();
            }

            if (!redisLock.checkTimeOut()) {
                LOGGER.info("Incrementing general counter TIMEOUT!");
            }
        } catch (Exception e) {
            LOGGER.error("Cannot get lock.", e);
        } finally {
            LOGGER.info("Releasing lock ");
            redisLock.release();
            jedis.close();
        }
    }

    private void incrementGeneralCounterInRedis() {
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

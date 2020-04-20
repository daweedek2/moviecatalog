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

    private RedisTemplate<String, String> redisTemplate;
    private StatisticService statisticService;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate, final StatisticService statisticService) {
        this.redisTemplate = redisTemplate;
        this.statisticService = statisticService;
    }

    public void updateMoviesInRedis(final List<Movie> movies, final String key) throws JsonProcessingException {
        String json = getJsonStringFromList(movies);
        LOGGER.info("Adding movies '{}' to the redis cache with key: '{}'", json, key);
        redisTemplate.opsForValue().set(key, json);
        statisticService.incrementSyncedRedisCounter();
    }

    public String getMoviesWithKey(final String key) {
        LOGGER.info("Getting movies from Redis cache with key '{}'.", key);
        statisticService.incrementSyncedRedisCounter();
        return redisTemplate.opsForValue().get(key);
    }

    private String getJsonStringFromList(final List<Movie> movies) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(movies);
    }

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
}

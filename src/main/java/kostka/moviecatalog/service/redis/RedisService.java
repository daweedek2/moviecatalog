package kostka.moviecatalog.service.redis;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.entity.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void updateMoviesInRedis(final List<Movie> movies, final String key) throws JsonProcessingException {
        String json = getJsonStringFromList(movies);
        LOGGER.info("Adding movies '{}' to the redis cache with key: '{}'", json, key);
        redisTemplate.opsForValue().set(key, json);
    }

    public String getMoviesWithKey(final String key) {
        LOGGER.info("Getting movies from Redis cache with key '{}'.", key);
        return redisTemplate.opsForValue().get(key);
    }

    private String getJsonStringFromList(final List<Movie> movies) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(movies);
    }
}

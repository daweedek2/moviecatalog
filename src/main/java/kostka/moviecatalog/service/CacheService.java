package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
public class CacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheService.class);
    private RedisService redisService;
    private ObjectMapper mapper = new ObjectMapper();

    @Autowired
    public CacheService(final RedisService redisService) {
        this.redisService = redisService;
    }

    public List<MovieListDto> getMoviesFromCacheWithKey(final String key) {
        LOGGER.info("get movies from redis cache with key '{}'", key);
        String json = this.getCachedDataJsonWithKey(key);
        if (json == null) {
            return Collections.emptyList();
        }
        try {
            return Arrays.asList(mapper.readValue(json, MovieListDto[].class));
        } catch (JsonProcessingException e) {
            LOGGER.error("Cannot get movies from json.", e);
        }
        return Collections.emptyList();
    }

    public <T> void cacheData(final String key, final T data) throws JsonProcessingException {
        LOGGER.info("saving data to cache with key '{}'", key);
        redisService.saveDataToRedisCache(key, prepareJsonData(data));
    }

    public String getCachedDataJsonWithKey(final String key) {
        LOGGER.info("getting data from cache with key '{}'", key);
        return redisService.getDataFromRedisCache(key);
    }

    private <T> String prepareJsonData(final T data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }
}

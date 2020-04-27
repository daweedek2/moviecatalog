package kostka.ratingservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static final String GENERAL_COUNTER = "general-counter";

    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
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
        LOGGER.info("RatingService - General counter is incremented from '{}' to '{}'.", oldValue, newValue);
    }
}

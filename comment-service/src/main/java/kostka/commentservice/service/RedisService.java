package kostka.commentservice.service;

import kostka.commentservice.configuration.RedisLockConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;

@Service
public class RedisService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisService.class);
    private static final String GENERAL_COUNTER = "general-counter";

    private RedisTemplate<String, String> redisTemplate;
    private Jedis jedis = new Jedis("localhost", 6379);
    private RedisLockConfiguration redisLock = new RedisLockConfiguration(jedis, "lock", 8000, 3000);

    @Autowired
    public RedisService(final RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void incrementGeneralCounterInRedis() {
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
        LOGGER.info("CommentService - General counter is incremented from '{}' to '{}'.", oldValue, newValue);
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
}
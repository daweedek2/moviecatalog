package kostka.moviecatalog.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

import java.util.UUID;

public class RedisLockConfiguration {
    private static final Logger LOGGER = LoggerFactory.getLogger(RedisLockConfiguration.class);
    /**
     * Client jedis
     */
    private Jedis jedis;
    /**
     * Lock key
     */
    private String key;
    /**
     * Lock timeout time in milliseconds
     */
    private int expire = 5000;
    /**
     * Gets the lock waiting time in milliseconds
     */
    private int timeout = 3000;
    /**
     * Does it possess locks?
     */
    private volatile boolean locked = false;
    /**
     * Unique Identification
     */
    private UUID uuid;

    /**
     * Thread Waiting Time
     */
    private static final int DEFAULT_ACQUIRE_RESOLUTION_MILLIS = 100;

    /**
     * lua script to delete key
     */
    private static final String LUA_DEL_SCRIPT =
            "if redis.call('GET',KEYS[1]) == ARGV[1] then return redis.call('DEL',KEYS[1]) else return 0 end";


    public RedisLockConfiguration(final Jedis jedis, final String key, final int timeout, final int expire) {
        this.jedis = jedis;
        this.key = key;
        this.timeout = timeout;
        this.expire = expire;
        this.uuid = UUID.randomUUID();
    }

    /**
     * set value
     * Note: This command can only be executed successfully without a key (NX option),
     * and the key has an automatic expiration time (PX attribute).
     *      The value of this key is a unique value. This value must be unique on all clients.
     *      The value of the same key can not be the same for all the acquirers (competitors).
     * @param value
     * @return
     */
    public String setNX(final String value) {
        SetParams setParams = new SetParams();
        setParams.nx();
        setParams.px(this.expire);
        return this.jedis.set(this.key, value, setParams);
    }

    /**
     * Acquisition locks
     * @return
     */
    public synchronized boolean lock() throws InterruptedException {
        long timeout = this.timeout;
        while (timeout > 0) {
            //Get the lock and return to OK represents the success of acquiring the lock.
            if ("OK".equals(this.setNX(this.getLockValue(Thread.currentThread().getId())))) {
                LOGGER.info("#######Acquire locks#######");
                this.locked = true;
                return true;
            }
            timeout -= DEFAULT_ACQUIRE_RESOLUTION_MILLIS;
            wait(DEFAULT_ACQUIRE_RESOLUTION_MILLIS);
        }
        return false;
    }

    /**
     * Release lock
     * Note: Delete by key and unique value
     * @return
     */
    public synchronized void release() {
        if (this.locked) {
            LOGGER.info("#######Release lock#######");
            long result = (long) this.jedis.eval(
                    LUA_DEL_SCRIPT, 1, this.key, this.getLockValue(Thread.currentThread().getId())
            );
            if (result > 0) {
                this.locked = false;
            }
        }

    }

    /**
     * Determine whether the current thread continues to have locks
     * Explanation: This method is mainly used to judge that the operation time has exceeded the expiration time of key,
     * and can be used for business rollover.
     * @return
     */
    public  boolean checkTimeOut() {
        if (this.locked) {
            String value = this.jedis.get(this.key);
            if (this.getLockValue(Thread.currentThread().getId()).equals(value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Value value of a lock (unique value)
     * Note: Value must be the only value that is primarily for safer release of the lock.
     * When releasing the lock, use a script to tell Redis that only the key exists
     * and the stored value is the same as the value I specified can tell me that the deletion is successful.
     * @param threadId
     * @return
     */
    public String getLockValue(final Long threadId) {
        return this.uuid.toString() + "_" + threadId;
    }

}

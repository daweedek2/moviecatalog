package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.StatisticDetail;
import kostka.moviecatalog.entity.counters.SynchronizedCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class StatisticService {
    public static final String REDIS_SYNCED_COUNTER = "redisSyncedCounter";
    public static final String RABBIT_MQ_SYNCED_COUNTER = "rabbitMqSyncedCounter";
    public static final String ELASTIC_SYNCED_COUNTER = "elasticSyncedCounter";
    public static final String SCHEDULED_SYNCED_COUNTER = "scheduledSyncedCounter";
    public static final String DB_SYNCED_COUNTER = "dbSyncedCounter";
    public static final int INITIAL_CAPACITY = 5;
    private SynchronizedCounter redisSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter rabbitMqSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter elasticSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter scheduledSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter dbSyncedCounter = new SynchronizedCounter();

    private Map<String, AtomicInteger> concurrentMap = new ConcurrentHashMap<>(INITIAL_CAPACITY);

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticService.class);

    public StatisticService() {
        concurrentMap.put(REDIS_SYNCED_COUNTER, new AtomicInteger(0));
        concurrentMap.put(RABBIT_MQ_SYNCED_COUNTER, new AtomicInteger(0));
        concurrentMap.put(ELASTIC_SYNCED_COUNTER, new AtomicInteger(0));
        concurrentMap.put(SCHEDULED_SYNCED_COUNTER, new AtomicInteger(0));
        concurrentMap.put(DB_SYNCED_COUNTER, new AtomicInteger(0));
    }

    @Async
    public void incrementSyncedRedisCounter() {
        LOGGER.info("incrementing sync redis counter started, old value = {}.", getSyncedRedisCounterValue());
        redisSyncedCounter.syncedIncrement();
        incrementMapWithKey(REDIS_SYNCED_COUNTER);
        LOGGER.info("incrementing sync redis counter finished, new value = {}.", getSyncedRedisCounterValue());
    }



    @Async
    public void incrementSyncedRabbitMqCounter() {
        LOGGER.info("incrementing sync rabbitMQ counter started, old value = {}.", getSyncedRabbitMqCounterValue());
        rabbitMqSyncedCounter.syncedIncrement();
        incrementMapWithKey(RABBIT_MQ_SYNCED_COUNTER);
        LOGGER.info("incrementing sync rabbitMQ counter finished, new value = {}.", getSyncedRabbitMqCounterValue());
    }

    @Async
    public void incrementSyncedElasticCounter() {
        LOGGER.info("incrementing sync elastic counter started, old value = {}.", getSyncedElasticCounterValue());
        elasticSyncedCounter.syncedIncrement();
        incrementMapWithKey(ELASTIC_SYNCED_COUNTER);
        LOGGER.info("incrementing sync elastic counter finished, new value = {}.", getSyncedElasticCounterValue());
    }

    @Async
    public void incrementSyncedScheduledCounter() {
        LOGGER.info("incrementing sync scheduled counter started, old value = {}.", getSyncedScheduledCounterValue());
        scheduledSyncedCounter.syncedIncrement();
        incrementMapWithKey(SCHEDULED_SYNCED_COUNTER);
        LOGGER.info("incrementing sync scheduled counter finished, new value = {}.", getSyncedScheduledCounterValue());
    }

    @Async
    public void incrementSyncedDbCounter() {
        LOGGER.info("incrementing sync scheduled counter started, old value = {}.", getSyncedDbCounterValue());
        dbSyncedCounter.syncedIncrement();
        incrementMapWithKey(DB_SYNCED_COUNTER);
        LOGGER.info("incrementing sync scheduled counter finished, new value = {}.", getSyncedDbCounterValue());
    }

    public int getSyncedRedisCounterValue() {
        return redisSyncedCounter.getCounterValue();
    }

    public int getSyncedScheduledCounterValue() {
        return scheduledSyncedCounter.getCounterValue();
    }

    public int getSyncedElasticCounterValue() {
        return elasticSyncedCounter.getCounterValue();
    }

    public int getSyncedRabbitMqCounterValue() {
        return rabbitMqSyncedCounter.getCounterValue();
    }

    public int getSyncedDbCounterValue() {
        return dbSyncedCounter.getCounterValue();
    }

    public StatisticDetail getAllStatistics() {
        StatisticDetail detail = new StatisticDetail();
        detail.setRedisCounter(this.getSyncedRedisCounterValue());
        detail.setRabbitCounter(this.getSyncedRabbitMqCounterValue());
        detail.setElasticCounter(this.getSyncedElasticCounterValue());
        detail.setScheduledCounter(this.getSyncedScheduledCounterValue());
        detail.setDbCounter(this.getSyncedDbCounterValue());
        return detail;
    }

    public Map<String, AtomicInteger> getStatisticMap() {
        return concurrentMap;
    }

    @Async
    public void incrementMapWithKey(final String key) {
        concurrentMap.get(key).incrementAndGet();
    }
}

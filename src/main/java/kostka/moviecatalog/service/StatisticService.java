package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.StatisticDetail;
import kostka.moviecatalog.entity.counters.SynchronizedCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class StatisticService {
    private SynchronizedCounter redisSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter rabbitMqSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter elasticSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter scheduledSyncedCounter = new SynchronizedCounter();
    private SynchronizedCounter dbCounter = new SynchronizedCounter();

    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticService.class);

    @Async
    public void incrementSyncedRedisCounter() {
        LOGGER.info("incrementing sync redis counter started, old value = {}.", getSyncedRedisCounterValue());
        redisSyncedCounter.syncedIncrement();
        LOGGER.info("incrementing sync redis counter finished, new value = {}.", getSyncedRedisCounterValue());
    }

    @Async
    public void incrementSyncedRabbitMqCounter() {
        LOGGER.info("incrementing sync rabbitMQ counter started, old value = {}.", getSyncedRabbitMqCounterValue());
        rabbitMqSyncedCounter.syncedIncrement();
        LOGGER.info("incrementing sync rabbitMQ counter finished, new value = {}.", getSyncedRabbitMqCounterValue());
    }

    @Async
    public void incrementSyncedElasticCounter() {
        LOGGER.info("incrementing sync elastic counter started, old value = {}.", getSyncedElasticCounterValue());
        elasticSyncedCounter.syncedIncrement();
        LOGGER.info("incrementing sync elastic counter finished, new value = {}.", getSyncedElasticCounterValue());
    }

    @Async
    public void incrementSyncedScheduledCounter() {
        LOGGER.info("incrementing sync scheduled counter started, old value = {}.", getSyncedScheduledCounterValue());
        scheduledSyncedCounter.syncedIncrement();
        LOGGER.info("incrementing sync scheduled counter finished, new value = {}.", getSyncedScheduledCounterValue());
    }

    @Async
    public void incrementSyncedDbCounter() {
        LOGGER.info("incrementing sync scheduled counter started, old value = {}.", getSyncedDbCounterValue());
        dbCounter.syncedIncrement();
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
        return dbCounter.getCounterValue();
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
}

package kostka.moviecatalog.entity;

public class StatisticDetail {
    private int redisCounter;
    private int rabbitCounter;
    private int elasticCounter;
    private int scheduledCounter;
    private int dbCounter;

    public int getDbCounter() {
        return dbCounter;
    }

    public void setDbCounter(final int dbCounter) {
        this.dbCounter = dbCounter;
    }

    public int getScheduledCounter() {
        return scheduledCounter;
    }

    public void setScheduledCounter(final int scheduledCounter) {
        this.scheduledCounter = scheduledCounter;
    }

    public int getRedisCounter() {
        return redisCounter;
    }

    public void setRedisCounter(final int redisCounter) {
        this.redisCounter = redisCounter;
    }

    public int getRabbitCounter() {
        return rabbitCounter;
    }

    public void setRabbitCounter(final int rabbitCounter) {
        this.rabbitCounter = rabbitCounter;
    }

    public int getElasticCounter() {
        return elasticCounter;
    }

    public void setElasticCounter(final int elasticCounter) {
        this.elasticCounter = elasticCounter;
    }
}

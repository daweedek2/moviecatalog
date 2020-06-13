package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.StatisticDetail;
import kostka.moviecatalog.service.StatisticService;
import kostka.moviecatalog.service.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static kostka.moviecatalog.service.redis.RedisService.GENERAL_COUNTER;

@RestController
@RequestMapping("/stats")
public class StatisticController {
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class);
    private StatisticService statisticService;
    private RedisService redisService;

    @Autowired
    public StatisticController(final StatisticService statisticService, final RedisService redisService) {
        this.statisticService = statisticService;
        this.redisService = redisService;
    }

    /**
     * Method for getting all statistics.
     * @return list of statistics.
     */
    @GetMapping()
    public StatisticDetail getAllStatistics() {
        LOGGER.info("get all counters values");
        return statisticService.getAllStatistics();
    }

    /**
     * Method for getting all statistics stored in map.
     * @return map of the statistics.
     */
    @GetMapping("/map")
    public Map<String, AtomicInteger> getAllStatisticsMap() {
        LOGGER.info("get all counters values in map");
        return statisticService.getStatisticMap();
    }

    /**
     * Method for getting all statistics stored in map.
     * @return map of the statistics.
     */
    @GetMapping("/generalCounter")
    public String getGeneralCounterValue() {
        LOGGER.info("get general counter value");
        return redisService.getDataFromRedisCache(GENERAL_COUNTER);
    }
}

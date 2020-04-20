package kostka.moviecatalog.controller;

import kostka.moviecatalog.entity.StatisticDetail;
import kostka.moviecatalog.service.StatisticService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/stats")
public class StatisticController {
    private StatisticService statisticService;
    private static final Logger LOGGER = LoggerFactory.getLogger(StatisticController.class);

    @Autowired
    public StatisticController(final StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping()
    public StatisticDetail getAllStatistics() {
        LOGGER.info("get all counters values");
        return statisticService.getAllStatistics();
    }

    @GetMapping("/map")
    public Map<String, AtomicInteger> getAllStatisticsMap() {
        LOGGER.info("get all counters values in map");
        return statisticService.getStatisticMap();
    }
}

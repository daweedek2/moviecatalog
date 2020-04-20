package kostka.moviecatalog.service.integrationTests;

import kostka.moviecatalog.service.ScheduledService;
import kostka.moviecatalog.service.StatisticService;
import kostka.moviecatalog.service.configuration.ScheduledConfig;
import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.time.Duration;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringJUnitConfig(ScheduledConfig.class)
public class ScheduledServiceIntegrationTest {

    @SpyBean
    private ScheduledService scheduledService;

    @MockBean
    private RabbitMqSender rabbitMqSender;

    @MockBean
    private StatisticService statisticService;

    @Test
    public void scheduledMethodIsCalledFixedDelayIntegrationTest() {
        await()
                .atMost(Duration.ofSeconds(119))
                .untilAsserted(() -> verify(scheduledService, atLeast(2)).updateAllMoviesInRedisAndFE());
    }
}

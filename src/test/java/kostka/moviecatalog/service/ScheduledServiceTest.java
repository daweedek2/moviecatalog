package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class ScheduledServiceTest {
    @InjectMocks
    private ScheduledService scheduledService;
    @Mock
    private RabbitMqSender rabbitMqSender;
    @Mock
    private StatisticService statisticService;

    @Test
    public void updateAllMoviesInRedisAndFETest() {
        scheduledService.updateAllMoviesInRedisAndFE();
        verify(rabbitMqSender).sendUpdateRequestToQueue();
    }
}

package kostka.moviecatalog.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ADMIN_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.MOVIE_DETAIL_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RECALCULATE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class STOMPServiceTest {
    @InjectMocks
    private STOMPService stompService;
    @Mock
    SimpMessagingTemplate simpMessagingTemplate;
    @Mock
    private StatisticService statisticService;

    @Test
    public void sendSTOMPToUpdateAllTablesTest() {
        stompService.sendSTOMPToUpdateAllTables();
        verify(simpMessagingTemplate).convertAndSend(eq(TOPIC + RECALCULATE_KEY),eq(RECALCULATE_KEY));
    }

    @Test
    public void sendSTOMPToRefreshMovieDetailTest() {
        stompService.sendSTOMPToRefreshMovieDetail();
        verify(simpMessagingTemplate).convertAndSend(eq(TOPIC + MOVIE_DETAIL_KEY),eq(MOVIE_DETAIL_KEY));
    }

    @Test
    public void sendSTOMPToRefreshAdminTest() {
        stompService.sendSTOMPToRefreshAdmin();
        verify(simpMessagingTemplate).convertAndSend(eq(TOPIC + ADMIN_KEY),eq(ADMIN_KEY));
    }
}

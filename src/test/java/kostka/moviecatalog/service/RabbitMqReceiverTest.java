package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver;
import kostka.moviecatalog.service.redis.RedisService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.ALL_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.LATEST_MOVIES_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOP_RATING_KEY;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMqReceiverTest {
    @InjectMocks
    private RabbitMqReceiver rabbitMqReceiver;
    @Mock
    private EsMovieService esMovieService;
    @Mock
    private RedisService redisService;
    @Mock
    private DbMovieService dbMovieService;
    @Mock
    private STOMPService stompService;
    @Mock
    private StatisticService statisticService;

    @Test
    public void receiveMessageCreateElasticQueueTest() {
        String id = "1";
        rabbitMqReceiver.receiveMessageCreateKeyElasticQueue(id);
        verify(esMovieService).createMovie(id);
    }

    @Test
    public void receiveMessageDeleteElasticQueueTest() {
        String id = "1";
        rabbitMqReceiver.receiveMessageDeleteKeyElasticQueue(id);
        verify(esMovieService).deleteEsMovie(id);
    }

    @Test
    public void receiveUpdateRequestRecalculateQueueTest() {
        rabbitMqReceiver.receiveUpdateRequestRecalculateQueue();
        verify(redisService).tryToUpdateMoviesInRedis(any(), eq(ALL_MOVIES_KEY));
        verify(redisService).tryToUpdateMoviesInRedis(any(), eq(TOP_RATING_KEY));
        verify(redisService).tryToUpdateMoviesInRedis(any(), eq(LATEST_MOVIES_KEY));
    }

    @Test
    public void receiveRefreshAdminRequestTest() {
        rabbitMqReceiver.receiveRefreshAdminRequest();
        verify(stompService).sendSTOMPToRefreshAdmin();
        verify(statisticService).incrementSyncedRabbitMqCounter();
    }
}

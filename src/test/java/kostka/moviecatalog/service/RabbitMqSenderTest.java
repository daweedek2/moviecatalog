package kostka.moviecatalog.service;

import kostka.moviecatalog.service.rabbitmq.RabbitMqSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.CREATE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.DELETE_MOVIE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.RECALCULATE_KEY;
import static kostka.moviecatalog.service.rabbitmq.RabbitMqReceiver.TOPIC_EXCHANGE;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class RabbitMqSenderTest {
    @InjectMocks
    private RabbitMqSender rabbitMqSender;
    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private StatisticService statisticService;

    @Test
    public void sendToCreateElasticQueueTest() {
        String movieName = "testName";
        rabbitMqSender.sendToCreateElasticQueue(movieName);
        verify(rabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE), eq(CREATE_MOVIE_KEY), eq(movieName));
    }

    @Test
    public void sendToDeleteElasticQueueTest() {
        String movieName = "testName";
        rabbitMqSender.sendToDeleteElasticQueue(movieName);
        verify(rabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE), eq(DELETE_MOVIE_KEY), eq(movieName));
    }

    @Test
    public void sendUpdateRequestToQueueTest() {
        rabbitMqSender.sendUpdateRequestToQueue();
        verify(rabbitTemplate).convertAndSend(eq(TOPIC_EXCHANGE), eq(RECALCULATE_KEY), eq(RECALCULATE_KEY));
    }
}

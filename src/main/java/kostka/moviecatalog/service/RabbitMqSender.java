package kostka.moviecatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static kostka.moviecatalog.configuration.RabbitMqConfiguration.QUEUE_NAME;

@Service
public class RabbitMqSender {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    private RabbitTemplate rabbitTemplate;

    @Autowired
    public RabbitMqSender(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void send(final String message) {
        LOGGER.info("Starting sending of message '{}'", message);
        rabbitTemplate.convertAndSend(QUEUE_NAME, message);
        LOGGER.info("Message is sent: '{}'", message);
    }
}

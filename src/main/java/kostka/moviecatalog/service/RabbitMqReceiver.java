package kostka.moviecatalog.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqReceiver {
    private static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    public void receiveMessage(final String message) {
        LOGGER.info("Recieved --> '{}'", message);
    }
}

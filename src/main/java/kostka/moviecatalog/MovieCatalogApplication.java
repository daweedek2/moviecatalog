package kostka.moviecatalog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieCatalogApplication {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    public static void main(final String[] args) {
        SpringApplication.run(MovieCatalogApplication.class, args);
        LOGGER.info("moviecatalog is running :)");
        LOGGER.debug("Debug message");
        LOGGER.error("Error just for testing!");
    }

}

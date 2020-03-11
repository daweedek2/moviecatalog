package kostka.moviecatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieCatalogApplication {
    static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogApplication.class);
    public static void main(final String[] args) {
        SpringApplication.run(MovieCatalogApplication.class, args);
        LOGGER.info("moviecatalog is running :)");
        LOGGER.debug("Debug message");
        LOGGER.error("Error just for testing!");
    }

}

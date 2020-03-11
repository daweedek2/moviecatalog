package kostka.moviecatalog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MovieCatalogApplication {
    public static void main(final String[] args) {
        Logger LOGGER = LoggerFactory.getLogger("jsonLogger");
        SpringApplication.run(MovieCatalogApplication.class, args);
        LOGGER.info("moviecatalog is running :)");
        LOGGER.debug("Debug message");
        LOGGER.trace("Trace message");
    }

}

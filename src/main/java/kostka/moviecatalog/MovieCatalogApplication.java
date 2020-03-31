package kostka.moviecatalog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableElasticsearchRepositories
@EnableCaching
@EnableScheduling
public class MovieCatalogApplication {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    public static void main(final String[] args) {
        SpringApplication.run(MovieCatalogApplication.class, args);
        LOGGER.info("moviecatalog is running :)");
    }

}

package kostka.ratingservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class RatingServiceApplication {
    static final Logger LOGGER = LoggerFactory.getLogger(RatingServiceApplication.class);

    public static void main(final String[] args) {
        SpringApplication.run(RatingServiceApplication.class, args);
        LOGGER.info("Rating Service is running.");
    }

}

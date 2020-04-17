package kostka.commentservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class CommentServiceApplication {
    static final Logger LOGGER = LoggerFactory.getLogger(CommentServiceApplication.class);
    public static void main(final String[] args) {
        SpringApplication.run(CommentServiceApplication.class, args);
        LOGGER.info("Comment Service is running.");
    }
}

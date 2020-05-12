package moviecatalog.shopservice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class ShopServiceApplication {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopServiceApplication.class);
    public static void main(final String[] args) {
        SpringApplication.run(ShopServiceApplication.class, args);
        LOGGER.info("Shop Service is running");
    }

}

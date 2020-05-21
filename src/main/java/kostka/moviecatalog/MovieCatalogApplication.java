package kostka.moviecatalog;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.annotation.Bean;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableElasticsearchRepositories
@EnableWebSocketMessageBroker
@EnableAsync
@EnableCaching
@EnableScheduling
@EnableEurekaClient
@EnableCircuitBreaker
@EnableWebSecurity
public class MovieCatalogApplication {
    static final Logger LOGGER = LoggerFactory.getLogger(MovieCatalogApplication.class);

    @Value("${rest-template.connection.timeout}")
    private Long connectionTimeout;

    @Value("${rest-template.read.timeout}")
    private Long readTimeout;

    @Bean
    @LoadBalanced
    public RestTemplate getRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    @Bean("webClient")
    @LoadBalanced
    public WebClient.Builder getWebClient() {
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.intValue())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)));
    }

    public static void main(final String[] args) {
        SpringApplication.run(MovieCatalogApplication.class, args);
        LOGGER.info("moviecatalog is running :)");
    }
}

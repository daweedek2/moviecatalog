package kostka.moviecatalog.configuration;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
public class CommunicationConfiguration {

    @Value("${connection.timeout}")
    private Long connectionTimeout;

    @Value("${read.timeout}")
    private Long readTimeout;

    @Value("${write.timeout}")
    private Long writeTimeout;

    @Bean
    @LoadBalanced
    @ConditionalOnProperty(
            name = "communication.web-client",
            havingValue = "false",
            matchIfMissing = true)
    public RestTemplate getRestTemplate(final RestTemplateBuilder restTemplateBuilder) {
        return restTemplateBuilder
                .setConnectTimeout(Duration.ofSeconds(connectionTimeout))
                .setReadTimeout(Duration.ofSeconds(readTimeout))
                .build();
    }

    @Bean("webClient")
    @LoadBalanced
    @ConditionalOnProperty(
            name = "communication.web-client",
            havingValue = "true")
    public WebClient.Builder getWebClient() {
        TcpClient tcpClient = TcpClient
                .create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, connectionTimeout.intValue())
                .doOnConnected(connection -> {
                    connection.addHandlerLast(new ReadTimeoutHandler(readTimeout, TimeUnit.MILLISECONDS));
                    connection.addHandlerLast(new WriteTimeoutHandler(writeTimeout, TimeUnit.MILLISECONDS));
                });
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.from(tcpClient)));
    }
}

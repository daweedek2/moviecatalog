package kostka.moviecatalog.service.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Service
@ConditionalOnProperty(
        name = "communication.web-client",
        havingValue = "true")
public class WebClientCommunicationServiceImpl implements CommunicationService {
    private WebClient.Builder webClient;

    @Autowired
    public WebClientCommunicationServiceImpl(@Qualifier("webClient") final WebClient.Builder webClient) {
        this.webClient = webClient;
    }

    @Override
    public <T> T sendGetRequest(final String url, final Class<T> responseType) {
        return webClient.build().get()
                .uri(url)
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }

    @Override
    public <T> T sendPostRequest(final String url, final Object dto, final Class<T> responseType) {
        return webClient.build().post()
                .uri(url)
                .body(BodyInserters.fromValue(dto))
                .retrieve()
                .bodyToMono(responseType)
                .block();
    }
}

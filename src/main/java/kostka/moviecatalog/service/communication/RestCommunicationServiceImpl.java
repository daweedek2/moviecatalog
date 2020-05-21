package kostka.moviecatalog.service.communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service("RestCommunicationService")
public class RestCommunicationServiceImpl implements CommunicationService {
    private RestTemplate restTemplate;

    @Autowired
    public RestCommunicationServiceImpl(final RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public <T> T sendGetRequest(final String url, final Class<T> responseType) {
        return restTemplate.getForObject(url, responseType);
    }

    @Override
    public <T> T sendPostRequest(final String url, final Object dto, final Class<T> responseType) {
        return restTemplate.postForObject(url, dto, responseType);
    }
}

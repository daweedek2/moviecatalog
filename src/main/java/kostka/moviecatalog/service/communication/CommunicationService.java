package kostka.moviecatalog.service.communication;

public interface CommunicationService {
     <T> T sendGetRequest(String url, Class<T> responseType);
     <T> T sendPostRequest(String url, Object dto, Class<T> responseType);
}

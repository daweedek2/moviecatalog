package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.entity.UserOrders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kostka.moviecatalog.service.ExternalCommentService.DEFAULT_ID;

@Service
public class ExternalShopService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalShopService.class);
    private RestTemplate restTemplate;
    private DbMovieService dbMovieService;
    private static final String SHOP_URL_SERVICE_DISCOVERY_CREATE = "http://shop-service/order/create";
    private static final String SHOP_URL_SERVICE_DISCOVERY_GET_USER_ORDERS = "http://shop-service/order/user/";
    private static final String SHOP_URL_SERVICE_DISCOVERY_ORDER_EXISTS_CHECK = "http://shop-service/order/checkOrder/";

    @Autowired
    public ExternalShopService(
            final RestTemplate restTemplate,
            final DbMovieService dbMovieService) {
        this.restTemplate = restTemplate;
        this.dbMovieService = dbMovieService;
    }

    @HystrixCommand(fallbackMethod = "buyMovieInShopServiceFallback")
    public Order buyMovieInShopService(final OrderDto dto) {
        LOGGER.info("request to buy movie in shop service");
        return restTemplate.postForObject(SHOP_URL_SERVICE_DISCOVERY_CREATE, dto, Order.class);
    }

    public Order buyMovieInShopServiceFallback(final OrderDto dto) {
        Order defaultOrder = new Order();
        defaultOrder.setMovieId(DEFAULT_ID);
        defaultOrder.setUserId(DEFAULT_ID);
        defaultOrder.setTimestamp(LocalDateTime.now());
        return defaultOrder;
    }

    @HystrixCommand(fallbackMethod = "getAlreadyBoughtMoviesForUserFallback")
    public List<MovieListDto> getAlreadyBoughtMoviesForUser(final Long userId) {
        UserOrders orders = restTemplate.getForObject(
                SHOP_URL_SERVICE_DISCOVERY_GET_USER_ORDERS + userId,
                UserOrders.class);
        if (orders.getOrders().isEmpty()) {
            return Collections.emptyList();
        }

        return Objects.requireNonNull(orders).getOrders()
                .stream()
                .map(order -> dbMovieService.getMovie(order.getMovieId()))
                .map(movie -> dbMovieService.fillMovieListDtoWithData(movie))
                .collect(Collectors.toList());
    }

    public List<MovieListDto> getAlreadyBoughtMoviesForUserFallback(final Long userId) {
        MovieListDto movieList = new MovieListDto();
        movieList.setId(DEFAULT_ID);
        movieList.setDescription("Shop Service is down");
        return Collections.singletonList(movieList);
    }

    @HystrixCommand(fallbackMethod = "checkAlreadyBoughtMovieForUserFallback")
    public Boolean checkAlreadyBoughtMovieForUser(final Long movieId, final Long userId) {
        return restTemplate.getForObject(
                SHOP_URL_SERVICE_DISCOVERY_ORDER_EXISTS_CHECK + movieId + "/" + userId,
                Boolean.class);
    }

    public Boolean checkAlreadyBoughtMovieForUserFallback(final Long movieId, final Long userId) {
        LOGGER.info("Shop Service is down, returning false to checkAlreadyBoughtMovieForUser method.");
        return false;
    }
}

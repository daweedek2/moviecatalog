package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.entity.UserOrders;
import kostka.moviecatalog.service.communication.CommunicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kostka.moviecatalog.service.ExternalCommentService.DEFAULT_ID;

@Service
public class ExternalShopService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalShopService.class);
    private static final String SHOP_SERVICE_URL = "http://shop-service/order/";
    private static final String CREATE = "create";
    private static final String GET_USER_ORDERS = "user/";
    private static final String CHECK_ORDER = "checkOrder/";
    private DbMovieService dbMovieService;
    private CommunicationService communicationService;

    @Autowired
    public ExternalShopService(
            final CommunicationService communicationService,
            final DbMovieService dbMovieService) {
        this.communicationService = communicationService;
        this.dbMovieService = dbMovieService;
    }

    @HystrixCommand(fallbackMethod = "buyMovieInShopServiceFallback")
    public Order buyMovieInShopService(final OrderDto dto) {
        LOGGER.info("request to buy movie in shop service");
        return communicationService.sendPostRequest(SHOP_SERVICE_URL + CREATE, dto, Order.class);
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
        UserOrders orders = communicationService.sendGetRequest(
                SHOP_SERVICE_URL + GET_USER_ORDERS + userId,
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
        return communicationService.sendGetRequest(
                SHOP_SERVICE_URL + CHECK_ORDER + movieId + "/" + userId,
                Boolean.class);
    }

    public Boolean checkAlreadyBoughtMovieForUserFallback(final Long movieId, final Long userId) {
        LOGGER.info("Shop Service is down, returning false to checkAlreadyBoughtMovieForUser method.");
        return false;
    }
}

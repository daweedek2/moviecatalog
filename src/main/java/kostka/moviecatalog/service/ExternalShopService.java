package kostka.moviecatalog.service;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import kostka.moviecatalog.dto.MovieListDto;
import kostka.moviecatalog.dto.OrderDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.Order;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.entity.UserOrders;
import kostka.moviecatalog.exception.UserNotAllowedToBuyMovieException;
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
import static kostka.moviecatalog.service.UserService.isUserAdultCheck;

@Service
public class ExternalShopService {
    public static final String SHOP_SERVICE_URL = "http://shop-service/order/";
    public static final String CREATE = "create";
    public static final String GET_USER_ORDERS = "user/";
    public static final String CHECK_ORDER = "checkOrder/";
    public static final String USER_HAS_ALREADY_BOUGHT_THIS_MOVIE = "User has already bought this movie.";
    public static final String USER_IS_TOO_YOUNG_TO_BUY_THIS_MOVIE = "User is too young to buy this movie.";
    public static final String USER_IS_BANNED = "User is banned.";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalShopService.class);
    private DbMovieService dbMovieService;
    private CommunicationService communicationService;
    private UserService userService;

    @Autowired
    public ExternalShopService(
            final CommunicationService communicationService,
            final DbMovieService dbMovieService,
            final UserService userService) {
        this.communicationService = communicationService;
        this.dbMovieService = dbMovieService;
        this.userService = userService;
    }

    @HystrixCommand(fallbackMethod = "buyMovieInShopServiceFallback")
    public Order buyMovieInShopService(final OrderDto dto) {
        LOGGER.info("request to buy movie in shop service");
        if (!isUserAllowedToBuyMovie(dto.getMovieId(), dto.getUserId())) {
            return null;
        }

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
        return Collections.emptyList();
    }

    @HystrixCommand(fallbackMethod = "checkAlreadyBoughtMovieForUserFallback")
    public boolean checkAlreadyBoughtMovieForUser(final Long movieId, final Long userId) {
        return communicationService.sendGetRequest(
                SHOP_SERVICE_URL + CHECK_ORDER + movieId + "/" + userId,
                Boolean.class);
    }

    public boolean checkAlreadyBoughtMovieForUserFallback(final Long movieId, final Long userId) {
        LOGGER.info("Shop Service is down, returning false to checkAlreadyBoughtMovieForUser method.");
        return false;
    }

    public boolean isUserAllowedToBuyMovie(final Long movieId, final Long userId) {
        try {
            validateUserAgeForMovie(movieId, userId);
            validateUserAlreadyBoughtMovie(movieId, userId);
            validateUserBanned(userId);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
            return false;
        }
        return true;
    }

    public void validateUserAgeForMovie(final Long movieId, final Long userId) {
        Movie movie = dbMovieService.getMovie(movieId);
        User user =  userService.getUser(userId);
        if (movie.isForAdults() && !isUserAdultCheck(user)) {
            LOGGER.info(USER_IS_TOO_YOUNG_TO_BUY_THIS_MOVIE);
            throw new UserNotAllowedToBuyMovieException(USER_IS_TOO_YOUNG_TO_BUY_THIS_MOVIE);
        }
    }

    public void validateUserAlreadyBoughtMovie(final Long movieId, final Long userId) {
        if (this.checkAlreadyBoughtMovieForUser(movieId, userId)) {
            LOGGER.info(USER_HAS_ALREADY_BOUGHT_THIS_MOVIE);
            throw new UserNotAllowedToBuyMovieException(USER_HAS_ALREADY_BOUGHT_THIS_MOVIE);
        }
    }

    public void validateUserBanned(final Long userId) {
        User user =  userService.getUser(userId);
        if (isUserBanned(user)) {
            LOGGER.info(USER_IS_BANNED);
            throw new UserNotAllowedToBuyMovieException(USER_IS_BANNED);
        }
    }

    public boolean isUserBanned(final User user) {
        return user.isBanned();
    }
}

package kostka.moviecatalog.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static kostka.moviecatalog.service.ExternalCommentService.DEFAULT_ID;
import static kostka.moviecatalog.service.ExternalCommentService.getKey;
import static kostka.moviecatalog.service.UserService.isUserAdultCheck;

@Service
public class ExternalShopService {
    public static final String SHOP_SERVICE_URL = "http://shop-service/order/";
    public static final String CREATE = "create";
    public static final String GET_USER_ORDERS = "user/";
    public static final String GET_MOVIE_ORDERS = "movie/";
    public static final String CHECK_ORDER = "checkOrder/";
    public static final String USER_HAS_ALREADY_BOUGHT_THIS_MOVIE = "User has already bought this movie.";
    public static final String USER_IS_TOO_YOUNG_TO_BUY_THIS_MOVIE = "User is too young to buy this movie.";
    public static final String USER_IS_BANNED = "User is banned.";
    public static final String COUNT = "count/";
    private static final String USER_MOVIES_KEY_PREFIX = "userBoughtMovies-";
    private static final String SOLDS_BY_USER_KEY_PREFIX = "userSoldsCount-";
    private static final String SOLDS_BY_MOVIE_KEY_PREFIX = "movieSoldsCount-";
    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalShopService.class);
    private DbMovieService dbMovieService;
    private CommunicationService communicationService;
    private UserService userService;
    private CacheService cacheService;
    private ObjectMapper mapper;

    @Autowired
    public ExternalShopService(
            final CommunicationService communicationService,
            final DbMovieService dbMovieService,
            final UserService userService,
            final CacheService cacheService,
            final ObjectMapper mapper) {
        this.communicationService = communicationService;
        this.dbMovieService = dbMovieService;
        this.userService = userService;
        this.cacheService = cacheService;
        this.mapper = mapper;
    }

    @PostConstruct
    public void setUp() {
        mapper.registerModule(new JavaTimeModule());
    }

    @HystrixCommand(fallbackMethod = "buyMovieInShopServiceFallback")
    public Order buyMovieInShopService(final OrderDto dto) {
        LOGGER.info("request to buy movie in shop service");
        validateUserToBuyMovie(dto.getMovieId(), dto.getUserId());

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
    public List<MovieListDto> getAlreadyBoughtMoviesForUser(final Long userId) throws JsonProcessingException {
        LOGGER.info("trying to get user orders from microservice.");
        UserOrders orders = communicationService.sendGetRequest(
                SHOP_SERVICE_URL + GET_USER_ORDERS + userId,
                UserOrders.class);
        if (orders.getOrders().isEmpty()) {
            return Collections.emptyList();
        }

        List<MovieListDto> boughtMovies = Objects.requireNonNull(orders).getOrders()
                .stream()
                .map(order -> dbMovieService.getMovie(order.getMovieId()))
                .map(movie -> dbMovieService.fillMovieListDtoWithData(movie))
                .collect(Collectors.toList());
        cacheService.cacheData(getKey(USER_MOVIES_KEY_PREFIX, userId), boughtMovies);
        return boughtMovies;
    }

    public List<MovieListDto> getAlreadyBoughtMoviesForUserFallback(final Long userId) throws JsonProcessingException {
        LOGGER.info("Shop Service is down, returning bought movies from cache.");
        String jsonData = cacheService.getCachedDataJsonWithKey(getKey(USER_MOVIES_KEY_PREFIX, userId));
        if (jsonData == null) {
            return Collections.emptyList();
        }
        return Arrays.asList(mapper.readValue(jsonData, MovieListDto[].class));
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

    @HystrixCommand(fallbackMethod = "getBoughtMoviesByUserCountFallback")
    public int getBoughtMoviesByUserCount(final Long userId) throws JsonProcessingException {
        LOGGER.info("getting count of bought movies for user with id '{}'", userId);
        int count = communicationService.sendGetRequest(
                SHOP_SERVICE_URL + GET_USER_ORDERS + COUNT + userId,
                int.class);
        cacheService.cacheData(SOLDS_BY_USER_KEY_PREFIX + userId, count);
        return count;
    }

    public int getBoughtMoviesByUserCountFallback(final Long userId) throws JsonProcessingException {
        LOGGER.info("Shop Service is down, return count of bought movies from cache.");
        String jsonData = cacheService.getCachedDataJsonWithKey(SOLDS_BY_USER_KEY_PREFIX + userId);
        if (jsonData == null) {
            return 0;
        }
        return mapper.readValue(jsonData, int.class);
    }

    @HystrixCommand(fallbackMethod = "getBoughtMoviesByMovieCountFallback")
    public int getBoughtMoviesByMovieCount(final Long movieId) throws JsonProcessingException {
        LOGGER.info("get count of solds for movie with id '{}'.", movieId);
        int count = communicationService.sendGetRequest(
                SHOP_SERVICE_URL + GET_MOVIE_ORDERS + COUNT + movieId,
                int.class);
        cacheService.cacheData(SOLDS_BY_MOVIE_KEY_PREFIX + movieId, count);
        return count;
    }

    public int getBoughtMoviesByMovieCountFallback(final Long movieId) throws JsonProcessingException {
        LOGGER.info("Shop Service is down, return count of bought movies from cache.");
        String jsonData = cacheService.getCachedDataJsonWithKey(SOLDS_BY_MOVIE_KEY_PREFIX + movieId);
        if (jsonData == null) {
            return 0;
        }
        return mapper.readValue(jsonData, int.class);
    }

    public void validateUserToBuyMovie(final Long movieId, final Long userId) {
        validateUserAgeForMovie(movieId, userId);
        validateUserAlreadyBoughtMovie(movieId, userId);
        validateUserBanned(userId);
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

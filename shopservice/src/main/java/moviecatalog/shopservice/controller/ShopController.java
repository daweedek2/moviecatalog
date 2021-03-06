package moviecatalog.shopservice.controller;

import moviecatalog.shopservice.dto.OrderDto;
import moviecatalog.shopservice.exception.InvalidDtoException;
import moviecatalog.shopservice.exception.OrderAlreadyExistsException;
import moviecatalog.shopservice.model.Order;
import moviecatalog.shopservice.model.UserOrders;
import moviecatalog.shopservice.service.MovieOrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/order")
public class ShopController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ShopController.class);
    private MovieOrderService movieOrderService;

    @Autowired
    public ShopController(final MovieOrderService movieOrderService) {
        this.movieOrderService = movieOrderService;
    }

    @PostMapping("/create")
    public Order createMovieOrder(final @Valid @RequestBody OrderDto dto) {
        LOGGER.info("create new order request");
        Order order = null;
        try {
            order = movieOrderService.createMovieOrder(dto);
        } catch (InvalidDtoException e) {
            LOGGER.error("Cannot create new order.", e);
        } catch (OrderAlreadyExistsException e) {
            LOGGER.error("User with id '{}' already bought movie with id '{}'", dto.getUserId(), dto.getMovieId(), e);
        }
        return order;
    }

    @GetMapping("/user/{userId}")
    public UserOrders getAllOrdersForUser(final @PathVariable Long userId) {
        LOGGER.info("getting user orders request");
        UserOrders userOrders = new UserOrders();
        userOrders.setOrders(movieOrderService.getAllOrdersForUser(userId));
        return userOrders;
    }

    @GetMapping("/checkOrder/{movieId}/{userId}")
    public boolean checkOrderAlreadyExists(
            final @PathVariable("movieId") Long movieId,
            final @PathVariable("userId") Long userId) {
        LOGGER.info("check already bought movie");
        return movieOrderService.isOrderAlreadyCreated(movieId, userId);
    }

    @GetMapping("/user/boughtCount/{userId}")
    public int getTotalCountOfBoughtMoviesForUser(final @PathVariable Long userId) {
        LOGGER.info("get total movies bought by user with id '{}'", userId);
        return movieOrderService.getTotalOrdersCountForUser(userId);
    }

    @GetMapping("/movie/soldCount/{movieId}")
    public int getTotalCountOfBoughtMoviesForMovie(final @PathVariable Long movieId) {
        LOGGER.info("get total solds of movie with id '{}'", movieId);
        return movieOrderService.getTotalOrdersCountForMovie(movieId);
    }
}

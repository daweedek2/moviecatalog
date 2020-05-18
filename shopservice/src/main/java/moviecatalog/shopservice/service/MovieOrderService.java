package moviecatalog.shopservice.service;

import moviecatalog.shopservice.dto.OrderDto;
import moviecatalog.shopservice.exception.OrderAlreadyExistsException;
import moviecatalog.shopservice.model.Order;
import moviecatalog.shopservice.repository.MovieOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The type Movie order service.
 */
@Service
public class MovieOrderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieOrderService.class);
    private MovieOrderRepository movieOrderRepository;

    /**
     * Instantiates a new Movie order service.
     *
     * @param movieOrderRepository the movie order repository
     */
    @Autowired
    public MovieOrderService(
            final MovieOrderRepository movieOrderRepository) {
        this.movieOrderRepository = movieOrderRepository;
    }

    /**
     * Create movie order.
     *
     * @param dto the dto
     * @return the movie order
     */
    public Order createMovieOrder(
            final OrderDto dto) {
        // check if movie order already exist for user id and movie id
        if (isOrderAlreadyCreated(dto.getMovieId(), dto.getUserId())) {
            LOGGER.info("Movie order cannot be created.");
            throw new OrderAlreadyExistsException();
        }

        LOGGER.info("Creating new movie order");
        Order newOrder = new Order();
        newOrder.setMovieId(dto.getMovieId());
        newOrder.setUserId(dto.getUserId());
        newOrder.setTimestamp(LocalDateTime.now());

        return saveMovieOrder(newOrder);
    }

    /**
     * Save movie order.
     *
     * @param order the movie order
     * @return the movie order
     */
    public Order saveMovieOrder(
            final Order order) {
        LOGGER.info("saving movie order");
        return movieOrderRepository.save(order);
    }

    /**
     * Gets specific movie order.
     *
     * @param movieId the movie id
     * @param userId  the user id
     * @return the specific movie order
     */
    public Order getSpecificMovieOrder(
            final Long movieId,
            final Long userId) {
        return movieOrderRepository.findByMovieIdAndUserId(movieId, userId);
    }

    /**
     * Gets all orders for user.
     *
     * @param userId the user id
     * @return the all orders for user
     */
    public List<Order> getAllOrdersForUser(
            final Long userId) {
        return movieOrderRepository.findAllByUserId(userId);
    }

    /**
     * Chekcs whether the order is already created.
     *
     * @param movieId the movie id
     * @param userId  the user id
     * @return the boolean
     */
    public Boolean isOrderAlreadyCreated(
            final Long movieId,
            final Long userId) {
        return this.getSpecificMovieOrder(movieId, userId) != null;
    }
}

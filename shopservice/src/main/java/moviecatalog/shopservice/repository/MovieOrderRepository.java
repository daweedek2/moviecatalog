package moviecatalog.shopservice.repository;

import moviecatalog.shopservice.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieOrderRepository extends JpaRepository<Order, Long> {
    Order findByMovieIdAndUserId(Long movieId, Long userId);
    List<Order> findAllByUserId(Long userId);
    int countByUserId(Long userId);
    int countByMovieId(Long movieId);
}

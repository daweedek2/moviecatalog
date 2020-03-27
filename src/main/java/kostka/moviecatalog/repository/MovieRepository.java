package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findMovieByName(String name);
    List<Movie> findByIdInOrderByIdDesc(List<Long> ids);
    List<Movie> findByIdInOrderByRatingDesc(List<Long> ids);
    List<Movie> findTop5ByOrderByRatingDesc();
}

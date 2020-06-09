package kostka.moviecatalog.repository;

import kostka.moviecatalog.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    List<Movie> findByIdInOrderByIdDesc(List<Long> ids);

    @Query(value = "SELECT * FROM movie m ORDER BY m.average_rating DESC LIMIT ?1",
            nativeQuery = true)
    List<Movie> findNTopRatedMovies(int limit);

    @Query(value = "SELECT * FROM movie m ORDER BY m.id DESC LIMIT ?1",
            nativeQuery = true)
    List<Movie> findNLatestMovies(int limit);
}

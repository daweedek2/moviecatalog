package kostka.moviecatalog.service;

import java.util.Optional;

public interface MovieService<T> {
    T createMovie(String name);
    T saveMovie(T movie);
    Optional<T> getMovie(Long movieId);
}

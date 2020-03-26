package kostka.moviecatalog.service;

import java.util.List;

public interface MovieService<T> {
    T createMovie(String name);
    T saveMovie(T movie);
    T getMovie(Long movieId);
    List<T> getAllMovies();
    List<T> fullTextSearch(String term);
    List<T> get5LatestMovies();
}

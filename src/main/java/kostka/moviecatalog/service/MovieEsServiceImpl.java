package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class MovieEsServiceImpl implements MovieService<EsMovie> {
    private MovieElasticSearchRepository movieElasticSearchRepository;

    @Autowired
    public MovieEsServiceImpl(final MovieElasticSearchRepository movieElasticSearchRepository) {
        this.movieElasticSearchRepository = movieElasticSearchRepository;
    }

    @Override
    public EsMovie createMovie(final String name) {
        EsMovie esMovie = new EsMovie();
        esMovie.setName(name);
        return saveMovie(esMovie);
    }

    @Override
    public EsMovie saveMovie(final EsMovie esMovie) {
        return movieElasticSearchRepository.save(esMovie);
    }

    @Override
    public EsMovie getMovie(final Long movieId) {
        return new EsMovie();
    }

    @Override
    public List<EsMovie> getAllMovies() {
        Iterable<EsMovie> movies = movieElasticSearchRepository.findAll();
        return StreamSupport.stream(movies.spliterator(), false).collect(Collectors.toList());
    }

    @Override
    public List<EsMovie> fullTextSearch(final String term) {
        Iterable<EsMovie> foundMovies = movieElasticSearchRepository.fullTextSearch(term);
        return StreamSupport.stream(foundMovies.spliterator(), false).collect(Collectors.toList());
    }
}

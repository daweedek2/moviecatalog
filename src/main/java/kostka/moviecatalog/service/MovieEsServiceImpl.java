package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
    public Optional<EsMovie> getMovie(final Long movieId) {
        return Optional.empty();
    }

    public Iterable<EsMovie> getAllMovies() {
        return movieElasticSearchRepository.findAll();
    }
}

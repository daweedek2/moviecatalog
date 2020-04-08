package kostka.moviecatalog.service;

import kostka.moviecatalog.configuration.MovieSpecification;
import kostka.moviecatalog.dto.SearchCriteriaDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieSpecificationService {
    private MovieRepository movieRepository;

    @Autowired
    public MovieSpecificationService(final MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    public List<Movie> getMoviesWithCriteria(final SearchCriteriaDto criteria) {
        MovieSpecification spec = new MovieSpecification(criteria);
        return movieRepository.findAll(spec);
    }
}

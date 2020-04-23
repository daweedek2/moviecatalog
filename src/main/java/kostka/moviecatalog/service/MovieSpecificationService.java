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
    private StatisticService statisticService;

    @Autowired
    public MovieSpecificationService(final MovieRepository movieRepository,
                                     final StatisticService statisticService) {
        this.movieRepository = movieRepository;
        this.statisticService = statisticService;
    }

    /**
     * Gets list of movies which match the provided search criteria.
     * @param criteria search criteria of the jpa specifications.
     * @return list of movies.
     */
    public List<Movie> getMoviesWithCriteria(final SearchCriteriaDto criteria) {
        MovieSpecification spec = new MovieSpecification(criteria);
        statisticService.incrementSyncedDbCounter();
        return movieRepository.findAll(spec);
    }
}

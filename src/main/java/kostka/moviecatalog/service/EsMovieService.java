package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsMovieService {
    static final Logger LOGGER = LoggerFactory.getLogger(EsMovieService.class);
    private MovieElasticSearchRepository movieElasticSearchRepository;
    private MovieRepository movieRepository;
    private StatisticService statisticService;

    @Autowired
    public EsMovieService(final MovieElasticSearchRepository movieElasticSearchRepository,
                          final MovieRepository movieRepository,
                          final StatisticService statisticService) {
        this.movieElasticSearchRepository = movieElasticSearchRepository;
        this.movieRepository = movieRepository;
        this.statisticService = statisticService;
    }

    public EsMovie createMovie(final String id) {
        Optional<Movie> optionalMovie = movieRepository.findById(Long.valueOf(id));
        statisticService.incrementSyncedElasticCounter();
        if (optionalMovie.isEmpty()) {
            LOGGER.info("Movie with Id '{}' not found in mysql DB.", id);
            throw new MovieNotFoundException();
        }
        Movie dbMovie = optionalMovie.get();
        EsMovie esMovie = this.populateEsMovieFromDbMovie(dbMovie);
        LOGGER.info("Movie with name '{}' is created in ElasticSearch", dbMovie.getName());
        return saveMovie(esMovie);
    }

    public EsMovie saveMovie(final EsMovie esMovie) {
        return movieElasticSearchRepository.save(esMovie);
    }

    public List<EsMovie> fullTextSearch(final String term) {
        statisticService.incrementSyncedElasticCounter();
        QueryStringQueryBuilder builder = this.getQueryBuilderForESFullTextSearchTerm(term);
        LOGGER.info("Searching in ElasticSearch for term '{}' in String fields of ElasticSearch Movie.", term);
        Iterable<EsMovie> foundMovies = movieElasticSearchRepository.search(builder);
        return StreamSupport.stream(foundMovies.spliterator(), false).collect(Collectors.toList());
    }

    public EsMovie populateEsMovieFromDbMovie(final Movie movie) {
        EsMovie esMovie = new EsMovie();
        esMovie.setId(movie.getId());
        esMovie.setName(movie.getName());
        esMovie.setDirector(movie.getDirector());
        esMovie.setDescription(movie.getDescription());
        return esMovie;
    }

    public QueryStringQueryBuilder getQueryBuilderForESFullTextSearchTerm(final String term) {
        QueryStringQueryBuilder builder = QueryBuilders
                .queryStringQuery(term)
                .autoGenerateSynonymsPhraseQuery(true);
        Set<Field> esMovieStringFields = Arrays.stream(EsMovie.class.getDeclaredFields())
                .filter(field -> field.getType().equals(String.class))
                .collect(Collectors.toSet());
        esMovieStringFields
                .forEach(field -> builder.field(field.getName()));
        return builder;
    }
}

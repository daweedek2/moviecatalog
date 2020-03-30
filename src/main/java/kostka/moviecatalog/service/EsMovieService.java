package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class EsMovieService {
    static final Logger LOGGER = LogManager.getLogger("CONSOLE_JSON_APPENDER");
    public static final String NAME_FIELD = "name";
    public static final String DESCRIPTION_FIELD = "description";
    public static final String DIRECTOR_FIELD = "director";
    private MovieElasticSearchRepository movieElasticSearchRepository;
    private MovieRepository movieRepository;

    @Autowired
    public EsMovieService(final MovieElasticSearchRepository movieElasticSearchRepository,
                          final MovieRepository movieRepository) {
        this.movieElasticSearchRepository = movieElasticSearchRepository;
        this.movieRepository = movieRepository;
    }

    public EsMovie createMovie(final String id) {
        Optional<Movie> optionalMovie = movieRepository.findById(Long.valueOf(id));
        if (optionalMovie.isEmpty()) {
            LOGGER.info("Movie with Id '{}' not found in mysql DB.", id);
            return null;
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
        LOGGER.info("Searching in ElasticSearch for term '{}' in fields {}, {}, {}",
                term, NAME_FIELD, DESCRIPTION_FIELD, DIRECTOR_FIELD);
        QueryStringQueryBuilder builder = QueryBuilders
                .queryStringQuery(term)
                .field(NAME_FIELD)
                .field(DESCRIPTION_FIELD)
                .field(DIRECTOR_FIELD)
                .autoGenerateSynonymsPhraseQuery(true);
        Iterable<EsMovie> foundMovies = movieElasticSearchRepository.search(builder);
        return StreamSupport.stream(foundMovies.spliterator(), false).collect(Collectors.toList());
    }

    private EsMovie populateEsMovieFromDbMovie(final Movie movie) {
        EsMovie esMovie = new EsMovie();
        esMovie.setId(movie.getId());
        esMovie.setName(movie.getName());
        esMovie.setDirector(movie.getDirector());
        esMovie.setDescription(movie.getDescription());
        return esMovie;
    }
}

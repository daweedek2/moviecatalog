package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.EsMovie;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieElasticSearchRepository;
import kostka.moviecatalog.repository.MovieRepository;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EsMovieServiceTest {
    private static final String TEST_NAME = "testName";
    private static final String TEST_DIRECTOR = "testDirector";
    private static final String TEST_DESCRIPTION = "testDescription";
    public static final String TEST_TERM = "test";
    public static final String NAME_FIELD = "name";
    public static final String DIRECTOR_FIELD = "director";
    public static final String DESCRIPTION_FIELD = "description";
    @InjectMocks
    EsMovieService esMovieService;

    @Mock
    MovieElasticSearchRepository movieElasticSearchRepository;

    @Mock
    MovieRepository movieRepository;

    @Mock
    private StatisticService statisticService;

    private Generator generator = new Generator();

    @Test
    public void createEsMovieSuccessfullyTest() {
        Movie dbMovie = generator.createMovieWithName(TEST_NAME);
        EsMovie esMovie = generator.createEsMovieWithName(TEST_NAME);
        when(movieRepository.findById(any())).thenReturn(Optional.of(dbMovie));
        when(movieElasticSearchRepository.save(any())).thenReturn(esMovie);

        EsMovie result = esMovieService.createMovie("1");

        assertThat(result).isEqualTo(esMovie);
    }

    @Test
    public void createEsMovieNotPresentInDbTest() {
        Movie dbMovie = generator.createMovieWithName(TEST_NAME);
        EsMovie esMovie = generator.createEsMovieWithName(TEST_NAME);
        when(movieRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> esMovieService.createMovie("1"))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    public void saveMovieTest() {
        EsMovie esMovie = generator.createEsMovieWithName(TEST_NAME);
        esMovieService.saveMovie(esMovie);

        verify(movieElasticSearchRepository).save(eq(esMovie));
    }

    @Test
    public void fullTextSearchUsesBuilderTest() {
        QueryStringQueryBuilder builder = esMovieService.getQueryBuilderForESFullTextSearchTerm(TEST_TERM);
        esMovieService.fullTextSearch(TEST_TERM);

        verify(movieElasticSearchRepository).search(eq(builder));
    }

    @Test
    public void fullTextSearchResultsTest() {
        EsMovie movie1 = generator.createEsMovieWithName(TEST_NAME);
        EsMovie movie2 = generator.createEsMovieWithName(TEST_NAME);
        List<EsMovie> esMovies = Arrays.asList(movie1, movie2);
        QueryStringQueryBuilder builder = esMovieService.getQueryBuilderForESFullTextSearchTerm(TEST_TERM);

        when(movieElasticSearchRepository.search(builder)).thenReturn(esMovies);

        List<EsMovie> result = esMovieService.fullTextSearch(TEST_TERM);

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie1, movie2);
    }

    @Test
    public void populateEsMovieFromDbMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        movie.setId(1L);
        movie.setDirector(TEST_DIRECTOR);
        movie.setDescription(TEST_DESCRIPTION);

        EsMovie esMovie = esMovieService.populateEsMovieFromDbMovie(movie);

        assertThat(esMovie.getId()).isEqualTo(movie.getId());
        assertThat(esMovie.getName()).isEqualTo(movie.getName());
        assertThat(esMovie.getDirector()).isEqualTo(movie.getDirector());
        assertThat(esMovie.getDescription()).isEqualTo(movie.getDescription());
    }

    @Test
    public void queryBuilderCreatedCorrectlyTest() {
        QueryStringQueryBuilder builder = QueryBuilders.queryStringQuery(TEST_TERM)
                .autoGenerateSynonymsPhraseQuery(true);
        builder.field(NAME_FIELD);
        builder.field(DIRECTOR_FIELD);
        builder.field(DESCRIPTION_FIELD);

        QueryStringQueryBuilder result = esMovieService.getQueryBuilderForESFullTextSearchTerm(TEST_TERM);
        assertThat(result).isEqualTo(builder);

    }
}

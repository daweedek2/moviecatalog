package kostka.moviecatalog.service;

import kostka.moviecatalog.dto.MovieFormDto;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.exception.InvalidDtoException;
import kostka.moviecatalog.exception.MovieNotFoundException;
import kostka.moviecatalog.repository.MovieRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class DbMovieServiceTest {
    private static final Long TEST_ID = 1L;
    private static final String TEST_NAME = "TestName";
    private static final String TEST_NAME_2 = "TestName2";

    @InjectMocks
    DbMovieService dbMovieService;

    @Mock
    MovieRepository movieRepository;

    @Mock
    EsMovieService esMovieService;

    @Mock
    private StatisticService statisticService;

    private Generator generator = new Generator();

    @Test
    public void createMovieValidDtoTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        MovieFormDto dto = generator.createValidMovieFormDto(TEST_NAME);
        when(movieRepository.save(any())).thenReturn(movie);

        Movie result = dbMovieService.createMovie(dto);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void createMovieInvalidDtoTest() {
        MovieFormDto dto = new MovieFormDto();

        assertThatThrownBy(() -> dbMovieService.createMovie(dto))
                .isInstanceOf(InvalidDtoException.class);
    }

    @Test
    public void saveMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        when(movieRepository.save(any())).thenReturn(movie);

        Movie result = dbMovieService.saveMovie(movie);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void getExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        when(movieRepository.findById(any())).thenReturn(Optional.of(movie));

        Movie result = dbMovieService.getMovie(1L);

        assertThat(result).isEqualTo(movie);
    }

    @Test
    public void getNonExistingMovieTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        when(movieRepository.findById(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> dbMovieService.getMovie(1L))
                .isInstanceOf(MovieNotFoundException.class);
    }

    @Test
    public void fullTextSearchFromESTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Long> ids = Arrays.asList(movie.getId(), movie2.getId());
        List<Movie> movies = Arrays.asList(movie, movie2);

        when(esMovieService.fullTextSearch(any())).thenReturn(anyList());
        when(movieRepository.findByIdInOrderByIdDesc(ids)).thenReturn(movies);

        List<Movie> result = dbMovieService.fullTextSearch("test");

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void fullTextSearchNoResultFromESTest() {
        List<Long> ids = Arrays.asList(1L, 2L);

        when(esMovieService.fullTextSearch(any())).thenReturn(anyList());
        when(movieRepository.findByIdInOrderByIdDesc(ids)).thenReturn(Collections.emptyList());

        List<Movie> result = dbMovieService.fullTextSearch("test");

        assertThat(result).isEmpty();
    }

    @Test
    public void getAllMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        when(movieRepository.findAll()).thenReturn(movies);

        List<Movie> result = dbMovieService.getAllMoviesFromDB();

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void getAllMoviesFromDBIsEmptyTest() {
        when(movieRepository.findAll()).thenReturn(Collections.emptyList());

        List<Movie> result = dbMovieService.getAllMoviesFromDB();

        assertThat(result).isEmpty();
    }

    @Test
    public void getTop5RatingMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        when(movieRepository.findNTopRatedMovies(eq(5))).thenReturn(movies);

        List<Movie> result = dbMovieService.getNTopRatedMoviesFromDB(5);

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void getLatestMoviesFromDBTest() {
        Movie movie = generator.createMovieWithName(TEST_NAME);
        Movie movie2 = generator.createMovieWithName(TEST_NAME_2);
        List<Movie> movies = Arrays.asList(movie, movie2);

        when(movieRepository.findNLatestMovies(eq(5))).thenReturn(movies);

        List<Movie> result = dbMovieService.getNLatestMoviesFromDB(5);

        assertThat(result).size().isEqualTo(2);
        assertThat(result).containsExactlyInAnyOrder(movie, movie2);
    }

    @Test
    public void deleteExistingMovieTest() {
        dbMovieService.deleteMovie(TEST_ID);
        verify(movieRepository).deleteById(eq(TEST_ID));
    }
}

package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.MovieComments;
import kostka.moviecatalog.entity.MovieDetail;
import kostka.moviecatalog.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class MovieDetailService {
    private DbMovieService movieService;
    private RestTemplate restTemplate;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDetailService.class);
    private static final String COMMENT_URL_SERVICE_DISCOVERY = "http://comment-service/comments/";
    private static final String COMMENT_URL_HARDCODED = "http://localhost:8082/comments/";

    @Autowired
    public MovieDetailService(final DbMovieService movieService, final RestTemplate restTemplate) {
        this.movieService = movieService;
        this.restTemplate = restTemplate;
    }

    public MovieDetail getMovieDetail(final Long movieId) {
        Movie movie = null;
        try {
            movie = movieService.getMovie(movieId);
        } catch (MovieNotFoundException e) {
            LOGGER.error("Movie not with id '{}' not found", movieId, e);
            movie = new Movie();
            movie.setName("Movie does not exists  with this id: " + movieId);
        }
        List<Comment> comments = this.getCommentsFromCommentService(movieId);
        LOGGER.info("Movie data are prepared.");
        return populateMovieDetail(movie, comments);
    }

    private MovieDetail populateMovieDetail(final Movie movie, final List<Comment> comments) {
        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setMovieId(movie.getId());
        movieDetail.setName(movie.getName());
        movieDetail.setDirector(movie.getDirector());
        movieDetail.setDescription(movie.getDescription());
        movieDetail.setRating(movie.getRating());
        movieDetail.setComments(comments);
        return movieDetail;
    }

    private List<Comment> getCommentsFromCommentService(final Long movieId) {
        LOGGER.info("Getting comments from Comment service.");
        MovieComments commentsResponse = restTemplate.getForObject(
                COMMENT_URL_HARDCODED + movieId,
                MovieComments.class);
        return Objects.requireNonNull(commentsResponse).getComments();
    }
}

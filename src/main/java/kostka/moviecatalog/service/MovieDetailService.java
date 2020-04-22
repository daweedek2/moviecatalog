package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.MovieDetail;
import kostka.moviecatalog.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieDetailService {
    private final DbMovieService movieService;
    private final ExternalCommentService externalCommentService;
    private final ExternalRatingService externalRatingService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDetailService.class);


    @Autowired
    public MovieDetailService(final DbMovieService movieService,
                              final ExternalCommentService externalCommentService,
                              final ExternalRatingService externalRatingService) {
        this.movieService = movieService;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
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

        List<Comment> comments = externalCommentService.getCommentsFromCommentService(movieId);
        double averageRating = externalRatingService.getAverageRatingFromRatingService(movieId);
        LOGGER.info("Movie data are prepared.");

        return populateMovieDetail(movie, comments, averageRating);
    }

    private MovieDetail populateMovieDetail(final Movie movie,
                                            final List<Comment> comments,
                                            final double averageRating) {
        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setMovieId(movie.getId());
        movieDetail.setName(movie.getName());
        movieDetail.setDirector(movie.getDirector());
        movieDetail.setDescription(movie.getDescription());
        movieDetail.setAverageRating(averageRating);
        movieDetail.setComments(comments);
        return movieDetail;
    }
}

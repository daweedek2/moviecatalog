package kostka.moviecatalog.service;

import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.MovieDetail;
import kostka.moviecatalog.entity.Rating;
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
    private final ExternalShopService externalShopService;
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDetailService.class);


    @Autowired
    public MovieDetailService(final DbMovieService movieService,
                              final ExternalCommentService externalCommentService,
                              final ExternalRatingService externalRatingService,
                              final ExternalShopService externalShopService) {
        this.movieService = movieService;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
        this.externalShopService = externalShopService;
    }

    /**
     * Gets detail of the movie with specific id. MovieDetail contains all movie data from db,
     * all comments from microservice Comment Service and all ratings from microservice Rating Service.
     * @param movieId id of the movie.
     * @return MovieDetail with all data.
     */
    public MovieDetail getMovieDetail(final Long movieId, final Long userId) {
        Movie movie = null;
        try {
            movie = movieService.getMovie(movieId);
        } catch (MovieNotFoundException e) {
            LOGGER.error("Movie not with id '{}' not found", movieId, e);
            movie = new Movie();
            movie.setName("Movie does not exists  with this id: " + movieId);
        }

        List<Comment> comments = externalCommentService.getCommentsFromCommentService(movieId);
        List<Rating> ratings = externalRatingService.getRatingsFromRatingService(movieId);
        Boolean isBoughtByUser = externalShopService.checkAlreadyBoughtMovieForUser(movieId, userId);
        LOGGER.info("Movie data are prepared.");

        return populateMovieDetail(movie, comments, ratings, isBoughtByUser);
    }

    public void setAverageRatingForMovie(final Long movieId) {
        Movie movie = movieService.getMovie(movieId);
        movie.setAverageRating(externalRatingService.getAverageRatingFromRatingService(movieId));
        movieService.saveMovie(movie);
    }

    /**
     * Collects data from the method parameters to MovieDetail entity.
     * @param movie movie data.
     * @param comments list of comments.
     * @param ratings list of ratings.
     * @return MovieDetail with all data from the method parameters.
     */
    private MovieDetail populateMovieDetail(final Movie movie,
                                            final List<Comment> comments,
                                            final List<Rating> ratings,
                                            final boolean isBoughtByUser) {
        MovieDetail movieDetail = new MovieDetail();
        movieDetail.setMovieId(movie.getId());
        movieDetail.setName(movie.getName());
        movieDetail.setDirector(movie.getDirector());
        movieDetail.setDescription(movie.getDescription());
        movieDetail.setAverageRating(movie.getAverageRating());
        movieDetail.setComments(comments);
        movieDetail.setRatings(ratings);
        movieDetail.setBought(isBoughtByUser);
        return movieDetail;
    }
}

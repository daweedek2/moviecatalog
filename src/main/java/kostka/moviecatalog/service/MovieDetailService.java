package kostka.moviecatalog.service;

import kostka.moviecatalog.builders.MovieBuilder;
import kostka.moviecatalog.builders.MovieDetailBuilder;
import kostka.moviecatalog.dto.CommentDetailDto;
import kostka.moviecatalog.dto.MovieDetailDto;
import kostka.moviecatalog.dto.RatingDetailDto;
import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.Movie;
import kostka.moviecatalog.entity.Rating;
import kostka.moviecatalog.entity.User;
import kostka.moviecatalog.exception.MovieNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieDetailService {
    private static final Logger LOGGER = LoggerFactory.getLogger(MovieDetailService.class);
    private final DbMovieService movieService;
    private final ExternalCommentService externalCommentService;
    private final ExternalRatingService externalRatingService;
    private final ExternalShopService externalShopService;
    private final UserService userService;

    @Autowired
    public MovieDetailService(final DbMovieService movieService,
                              final ExternalCommentService externalCommentService,
                              final ExternalRatingService externalRatingService,
                              final ExternalShopService externalShopService,
                              final UserService userService) {
        this.movieService = movieService;
        this.externalCommentService = externalCommentService;
        this.externalRatingService = externalRatingService;
        this.externalShopService = externalShopService;
        this.userService = userService;
    }

    /**
     * Gets detail of the movie with specific id. MovieDetail contains all movie data from db,
     * all comments from microservice Comment Service and all ratings from microservice Rating Service.
     * @param movieId id of the movie.
     * @return MovieDetail with all data.
     */
    public MovieDetailDto getMovieDetail(final Long movieId, final Long userId) {
        Movie movie = null;
        try {
            movie = movieService.getMovie(movieId);
        } catch (MovieNotFoundException e) {
            LOGGER.error("Movie not with id '{}' not found", movieId, e);
            movie = new MovieBuilder()
                    .setName("Movie does not exists  with this id: " + movieId)
                    .build();
        }

        List<Comment> comments = externalCommentService.getCommentsFromCommentService(movieId);
        List<Rating> ratings = externalRatingService.getRatingsFromRatingService(movieId);
        boolean isBoughtByUser = externalShopService.checkAlreadyBoughtMovieForUser(movieId, userId);
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
    private MovieDetailDto populateMovieDetail(final Movie movie,
                                               final List<Comment> comments,
                                               final List<Rating> ratings,
                                               final boolean isBoughtByUser) {
        return new MovieDetailBuilder()
                .setMovieId(movie.getId())
                .setName(movie.getName())
                .setDirector(movie.getDirector())
                .setDescription(movie.getDescription())
                .setAverageRating(movie.getAverageRating())
                .setComments(getCommentDtoListForMovieDetail(comments))
                .setRatings(getRatingDtoListForMovieDetail(ratings))
                .setBought(isBoughtByUser)
                .setForAdults(movie.isForAdults())
                .build();
    }

    private List<CommentDetailDto> getCommentDtoListForMovieDetail(final List<Comment> comments) {
         return comments.stream()
                .map(this::getCommentDtoForMovieDetail)
                .collect(Collectors.toList());
    }

    private CommentDetailDto getCommentDtoForMovieDetail(final Comment comment) {
        User user = userService.getUser(comment.getAuthorId());
        return new CommentDetailDto(comment.getCommentId(), user.getFullName(),
                comment.getCommentText(), user.getUserId());
    }

    private List<RatingDetailDto> getRatingDtoListForMovieDetail(final List<Rating> ratings) {
        return ratings.stream()
                .map(this::getRatingDtoForMovieDetail)
                .collect(Collectors.toList());
    }

    private RatingDetailDto getRatingDtoForMovieDetail(final Rating rating) {
        User user = userService.getUser(rating.getAuthorId());
        return new RatingDetailDto(rating.getRatingId(), rating.getRatingValue(),
                user.getFullName(), user.getUserId());
    }
}

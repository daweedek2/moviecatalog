package kostka.moviecatalog.entity;

import java.util.List;

/**
 * Stands for transferring data from external microservice (RatingService).
 */
public class MovieRating {
    private List<Rating> movieRatings;

    public MovieRating(final List<Rating> movieRatings) {
        this.movieRatings = movieRatings;
    }

    public MovieRating() {
    }

    public List<Rating> getMovieRatings() {
        return movieRatings;
    }

    public void setMovieRatings(final List<Rating> movieRatings) {
        this.movieRatings = movieRatings;
    }
}

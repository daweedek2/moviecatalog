package kostka.ratingservice.model;

import java.util.List;

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

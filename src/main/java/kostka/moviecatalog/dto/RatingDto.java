package kostka.moviecatalog.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class RatingDto {
    public static final int MIN_RATING_VALUE = 0;
    public static final int MAX_RATING_VALUE = 10;

    @NotNull(message = "Id cannot be empty.")
    private Long movieId;
    @NotNull(message = "Rating cannot be empty.")
    @Range(min = MIN_RATING_VALUE, max = MAX_RATING_VALUE)
    private int rating;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(final int rating) {
        this.rating = rating;
    }
}

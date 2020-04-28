package kostka.moviecatalog.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Used for transferring data from FE to BE during creation of new rating.
 */
public class RatingDto {
    public static final int MIN_RATING_VALUE = 0;
    public static final int MAX_RATING_VALUE = 10;

    @NotNull(message = "Id cannot be empty.")
    private Long movieId;
    @NotNull(message = "Rating cannot be empty.")
    @Range(min = MIN_RATING_VALUE, max = MAX_RATING_VALUE)
    private int ratingValue;
    private Long authorId;

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(final int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final Long authorId) {
        this.authorId = authorId;
    }
}

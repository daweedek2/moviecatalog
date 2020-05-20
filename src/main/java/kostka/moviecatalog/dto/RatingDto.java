package kostka.moviecatalog.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * Used for transferring data from FE to BE during creation of new rating.
 */
public class RatingDto {
    public static final int MIN_RATING_VALUE = 1;
    public static final int MAX_RATING_VALUE = 10;

    @NotNull(message = "Id cannot be empty.")
    private Long id;
    @NotNull(message = "Rating cannot be empty.")
    @Range(min = MIN_RATING_VALUE, max = MAX_RATING_VALUE)
    private int ratingValue;
    private Long authorId;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
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

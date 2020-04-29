package kostka.ratingservice.dto;

import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

public class RatingDto {
    public static final int MIN_RATING_VALUE = 0;
    public static final int MAX_RATING_VALUE = 10;

    @NotNull(message = "Id cannot be empty.")
    private Long id;
    @NotNull(message = "Rating cannot be empty.")
    @Range(min = MIN_RATING_VALUE, max = MAX_RATING_VALUE)
    private int ratingValue;
    private Long authorId;


    public RatingDto(final Long id, final Long authorId, final int ratingValue) {
        this.id = id;
        this.authorId = authorId;
        this.ratingValue = ratingValue;
    }

    public RatingDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final Long authorId) {
        this.authorId = authorId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(final int ratingValue) {
        this.ratingValue = ratingValue;
    }
}

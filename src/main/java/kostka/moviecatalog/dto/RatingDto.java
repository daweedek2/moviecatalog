package kostka.moviecatalog.dto;

import javax.validation.constraints.NotNull;

public class RatingDto {
    @NotNull(message = "Id cannot be empty.")
    private Long id;
    @NotNull(message = "Rating cannot be empty.")
    private int rating;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(final int rating) {
        this.rating = rating;
    }
}

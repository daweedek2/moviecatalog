package kostka.ratingservice.dto;

public class RatingDto {
    private Long movieId;
    private Long userId;
    private int ratingValue;

    public RatingDto(final Long movieId, final Long userId, final int ratingValue) {
        this.movieId = movieId;
        this.userId = userId;
        this.ratingValue = ratingValue;
    }

    public RatingDto() {
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(final int ratingValue) {
        this.ratingValue = ratingValue;
    }
}

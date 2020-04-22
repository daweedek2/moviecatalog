package kostka.moviecatalog.entity;

public class Rating {
    private Long ratingId;
    private Long movieId;
    private Long userId;
    private int ratingValue;

    public Rating() {
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(final Long ratingId) {
        this.ratingId = ratingId;
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

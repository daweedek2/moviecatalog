package kostka.moviecatalog.entity;

public class Rating {
    private Long ratingId;
    private Long movieId;
    private Long authorId;
    private int ratingValue;

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

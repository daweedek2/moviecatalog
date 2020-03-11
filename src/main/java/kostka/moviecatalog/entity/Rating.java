package kostka.moviecatalog.entity;

public class Rating {
    private Long movieId;
    private int value;

    public Rating(final Long movieId, final int value) {
        this.movieId = movieId;
        this.value = value;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public int getValue() {
        return value;
    }

    public void setValue(final int value) {
        this.value = value;
    }
}

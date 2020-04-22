package kostka.ratingservice.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "rating")
public class Rating {
    @Id
    @GeneratedValue
    private Long ratingId;
    private Long movieId;
    private Long authorId;
    private int ratingValue;

    public Rating(final Long ratingId, final Long movieId, final Long authorId, final int ratingValue) {
    }

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

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final Long userId) {
        this.authorId = userId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(final int ratingValue) {
        this.ratingValue = ratingValue;
    }
}

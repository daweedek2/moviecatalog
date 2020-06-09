package kostka.moviecatalog.dto;

/**
 * Used for transferring data from FE to BE during creation of new rating.
 */
public class RatingDetailDto {
    private Long ratingId;
    private int ratingValue;
    private String authorName;
    private Long userId;

    public RatingDetailDto(final Long ratingId, final int ratingValue, final String authorName, final Long userId) {
        this.ratingId = ratingId;
        this.ratingValue = ratingValue;
        this.authorName = authorName;
        this.userId = userId;
    }

    public Long getRatingId() {
        return ratingId;
    }

    public void setRatingId(final Long ratingId) {
        this.ratingId = ratingId;
    }

    public int getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(final int ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(final String authorName) {
        this.authorName = authorName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}

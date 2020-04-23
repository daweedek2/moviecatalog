package kostka.ratingservice.model;

/***
 * Entity which holds the average rating value of movie for transferring to external microservice (movie catalog).
 */
public class AverageRating {
    private double averageRatingValue;

    public double getAverageRatingValue() {
        return averageRatingValue;
    }

    public void setAverageRatingValue(final double averageRatingValue) {
        this.averageRatingValue = averageRatingValue;
    }
}

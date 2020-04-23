package kostka.moviecatalog.entity;

/**
 * Stands for transferring data from external microservice (RatingService).
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

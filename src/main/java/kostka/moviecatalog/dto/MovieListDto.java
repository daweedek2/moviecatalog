package kostka.moviecatalog.dto;

/**
 * Used for displaying movie details in the movie lists.
 */
public final class MovieListDto {
    private Long id;
    private String name;
    private String description;
    private double averageRating;
    private boolean forAdults;

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(final double averageRating) {
        this.averageRating = averageRating;
    }

    public boolean isForAdults() {
        return forAdults;
    }

    public void setForAdults(final boolean forAdults) {
        this.forAdults = forAdults;
    }
}

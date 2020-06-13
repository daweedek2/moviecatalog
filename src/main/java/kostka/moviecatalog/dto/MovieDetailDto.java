package kostka.moviecatalog.dto;

import java.util.List;

/**
 * Stands for transferring data from BE to FE.
 */
public class MovieDetailDto {
    private Long movieId;
    private String name;
    private String director;
    private String description;
    private double averageRating;
    private boolean bought;
    private boolean forAdults;
    private List<CommentDetailDto> comments;
    private List<RatingDetailDto> ratings;
    private int soldsCount;

    public MovieDetailDto() {
    }

    public MovieDetailDto(final Long movieId, final String name, final String director, final String description,
                       final double averageRating, final boolean bought, final boolean forAdults,
                       final List<CommentDetailDto> comments, final List<RatingDetailDto> ratings,
                       final int soldsCount) {
        this.movieId = movieId;
        this.name = name;
        this.director = director;
        this.description = description;
        this.averageRating = averageRating;
        this.bought = bought;
        this.forAdults = forAdults;
        this.comments = comments;
        this.ratings = ratings;
        this.soldsCount = soldsCount;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(final String director) {
        this.director = director;
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

    public List<CommentDetailDto> getComments() {
        return comments;
    }

    public void setComments(final List<CommentDetailDto> comments) {
        this.comments = comments;
    }

    public List<RatingDetailDto> getRatings() {
        return ratings;
    }

    public void setRatings(final List<RatingDetailDto> ratings) {
        this.ratings = ratings;
    }

    public boolean isBought() {
        return bought;
    }

    public void setBought(final boolean bought) {
        this.bought = bought;
    }

    public boolean isForAdults() {
        return forAdults;
    }

    public void setForAdults(final boolean forAdults) {
        this.forAdults = forAdults;
    }

    public int getSoldsCount() {
        return soldsCount;
    }

    public void setSoldsCount(final int soldsCount) {
        this.soldsCount = soldsCount;
    }
}

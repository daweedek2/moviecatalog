package kostka.moviecatalog.entity;

import java.util.List;

public class MovieDetail {
    private Long movieId;
    private String name;
    private String director;
    private String description;
    private double averageRating;
    private List<Comment> comments;

    public MovieDetail() {
    }

    public MovieDetail(final Long movieId, final String name, final String director,
                       final String description, final double averageRating, final List<Comment> comments) {
        this.movieId = movieId;
        this.name = name;
        this.director = director;
        this.description = description;
        this.averageRating = averageRating;
        this.comments = comments;
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

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(final List<Comment> comments) {
        this.comments = comments;
    }
}

package kostka.moviecatalog.builders;

import kostka.moviecatalog.entity.Comment;
import kostka.moviecatalog.entity.MovieDetail;
import kostka.moviecatalog.entity.Rating;

import java.util.List;

/**
 * Builder for building MovieDetail.
 */
public class MovieDetailBuilder {
    private Long movieId;
    private String name;
    private String director;
    private String description;
    private double averageRating;
    private boolean bought;
    private boolean forAdults;
    private List<Comment> comments;
    private List<Rating> ratings;

    public MovieDetailBuilder() {
    }

    public MovieDetailBuilder setMovieId(Long movieId) {
        this.movieId = movieId;
        return this;
    }

    public MovieDetailBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MovieDetailBuilder setDirector(String director) {
        this.director = director;
        return this;
    }

    public MovieDetailBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MovieDetailBuilder setAverageRating(double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    public MovieDetailBuilder setBought(boolean bought) {
        this.bought = bought;
        return this;
    }

    public MovieDetailBuilder setForAdults(boolean forAdults) {
        this.forAdults = forAdults;
        return this;
    }

    public MovieDetailBuilder setComments(List<Comment> comments) {
        this.comments = comments;
        return this;
    }

    public MovieDetailBuilder setRatings(List<Rating> ratings) {
        this.ratings = ratings;
        return this;
    }

    public MovieDetail build() {
        return new MovieDetail(movieId, name, director, description, averageRating,
                bought, forAdults, comments, ratings);
    }
}

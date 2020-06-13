package kostka.moviecatalog.builders;

import kostka.moviecatalog.dto.CommentDetailDto;
import kostka.moviecatalog.dto.MovieDetailDto;
import kostka.moviecatalog.dto.RatingDetailDto;

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
    private List<CommentDetailDto> comments;
    private List<RatingDetailDto> ratings;
    private int soldsCount;

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

    public MovieDetailBuilder setComments(List<CommentDetailDto> comments) {
        this.comments = comments;
        return this;
    }

    public MovieDetailBuilder setRatings(List<RatingDetailDto> ratings) {
        this.ratings = ratings;
        return this;
    }

    public MovieDetailBuilder setSoldsCount(int soldsCount) {
        this.soldsCount = soldsCount;
        return this;
    }

        public MovieDetailDto build() {
        return new MovieDetailDto(movieId, name, director, description, averageRating,
                bought, forAdults, comments, ratings, soldsCount);
    }
}

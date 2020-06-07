package kostka.moviecatalog.builders;

import kostka.moviecatalog.entity.Movie;

/**
 * Builder for building Movie entity.
 */
public class MovieBuilder {
    private Long id;
    private String name;
    private String director;
    private String camera;
    private String music;
    private String description;
    private double averageRating;
    private boolean forAdults;

    public MovieBuilder() {
    }

    public MovieBuilder setId(Long id) {
        this.id = id;
        return this;
    }

    public MovieBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public MovieBuilder setDirector(String director) {
        this.director = director;
        return this;
    }

    public MovieBuilder setCamera(String camera) {
        this.camera = camera;
        return this;
    }

    public MovieBuilder setMusic(String music) {
        this.music = music;
        return this;
    }

    public MovieBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public MovieBuilder setAverageRating(double averageRating) {
        this.averageRating = averageRating;
        return this;
    }

    public MovieBuilder setForAdults(boolean forAdults) {
        this.forAdults = forAdults;
        return this;
    }

    public Movie build() {
        return new Movie(id, name, director, camera, music, description, averageRating, forAdults);
    }
}

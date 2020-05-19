package kostka.moviecatalog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue
    private Long id;
    @Column(unique = true)
    @NotEmpty
    private String name;
    @Column
    private String director;
    @Column
    private String camera;
    @Column
    private String music;
    @Column
    private String description;
    @Column
    private double averageRating;
    @Column
    private boolean forAdults;

    public Movie() {
    }

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

    public String getDirector() {
        return director;
    }

    public void setDirector(final String director) {
        this.director = director;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(final String camera) {
        this.camera = camera;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(final String music) {
        this.music = music;
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

    public void setAverageRating(final double rating) {
        this.averageRating = rating;
    }

    public boolean isForAdults() {
        return forAdults;
    }

    public void setForAdults(final boolean forAdults) {
        this.forAdults = forAdults;
    }
}

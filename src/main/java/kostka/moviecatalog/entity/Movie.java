package kostka.moviecatalog.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "movie")
public class Movie {
    @Id
    @GeneratedValue
    private Long id;
    @Column
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
    private Integer rating;

    public Movie(final Long id,
                 final String name,
                 final String director,
                 final String camera,
                 final String music,
                 final String description,
                 final Integer rating) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.camera = camera;
        this.music = music;
        this.description = description;
        this.rating = rating;
    }

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

    public Integer getRating() {
        return rating;
    }

    public void setRating(final Integer rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", director='" + director + '\'' +
                ", camera='" + camera + '\'' +
                ", music='" + music + '\'' +
                ", description='" + description + '\'' +
                ", rating=" + rating +
                '}';
    }
}

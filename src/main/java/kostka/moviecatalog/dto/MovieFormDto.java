package kostka.moviecatalog.dto;

import javax.validation.constraints.NotEmpty;

/**
 * Used for transferring data from FE to BE during creation of new movie.
 */
public class MovieFormDto {
    @NotEmpty(message = "Name cannot be empty.")
    private String name;
    private String director;
    private String camera;
    private String music;
    private String description;

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
}

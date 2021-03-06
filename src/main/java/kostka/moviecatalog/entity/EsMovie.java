package kostka.moviecatalog.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.CompletionField;
import org.springframework.data.elasticsearch.annotations.Document;


@Document(indexName = "movie")
public class EsMovie {

    @Id
    private Long id;
    @CompletionField
    private String name;
    @CompletionField
    private String director;
    @CompletionField
    private String description;

    public EsMovie() {
    }

    public EsMovie(final Long id, final String name, final String director, final String description) {
        this.id = id;
        this.name = name;
        this.director = director;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }
}

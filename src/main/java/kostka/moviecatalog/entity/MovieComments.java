package kostka.moviecatalog.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * Stands for transferring data from external microservice (CommentService).
 */
public class MovieComments {
    private List<Comment> comments = new ArrayList<>();

    public MovieComments(final List<Comment> comments) {
        this.comments = comments;
    }

    public MovieComments() {
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(final List<Comment> comments) {
        this.comments = comments;
    }
}

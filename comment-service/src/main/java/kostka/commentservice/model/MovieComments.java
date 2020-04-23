package kostka.commentservice.model;

import java.util.ArrayList;
import java.util.List;

/***
 * Entity which is used for transferring list of comments to another microservice (moviecatalog).
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

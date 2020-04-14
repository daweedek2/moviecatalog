package kostka.commentservice.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue
    private Long commentId;
    @Column
    private Long movieId;
    @Column
    private Long authorId;
    @Column
    private String commentText;

    public Comment() {
    }

    public Comment(final Long commentId, final Long movieId, final Long authorId, final String commentText) {
        this.commentId = commentId;
        this.movieId = movieId;
        this.authorId = authorId;
        this.commentText = commentText;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(final Long commentId) {
        this.commentId = commentId;
    }

    public Long getMovieId() {
        return movieId;
    }

    public void setMovieId(final Long movieId) {
        this.movieId = movieId;
    }

    public Long getAuthorId() {
        return authorId;
    }

    public void setAuthorId(final Long authorId) {
        this.authorId = authorId;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(final String commentText) {
        this.commentText = commentText;
    }
}

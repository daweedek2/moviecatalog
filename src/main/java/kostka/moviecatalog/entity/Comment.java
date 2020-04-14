package kostka.moviecatalog.entity;

public class Comment {
    private Long commentId;
    private Long movieId;
    private Long authorId;
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

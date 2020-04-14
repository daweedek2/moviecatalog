package kostka.commentservice.dto;

public class CommentDto {
    private Long movieId;
    private Long authorId;
    private String commentText;

    public CommentDto(final Long movieId, final Long authorId, final String commentText) {
        this.movieId = movieId;
        this.authorId = authorId;
        this.commentText = commentText;
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

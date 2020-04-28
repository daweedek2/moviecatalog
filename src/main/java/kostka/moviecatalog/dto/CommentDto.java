package kostka.moviecatalog.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class CommentDto {
    @NotNull(message = "Id cannot be empty.")
    private Long movieId;
    private Long authorId;
    @NotEmpty(message = "Text cannot be empty.")
    private String commentText;

    public CommentDto(final Long movieId, final Long authorId, final String commentText) {
        this.movieId = movieId;
        this.authorId = authorId;
        this.commentText = commentText;
    }

    public CommentDto() {
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

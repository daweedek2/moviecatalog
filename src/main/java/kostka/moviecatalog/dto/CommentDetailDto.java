package kostka.moviecatalog.dto;

public class CommentDetailDto {
    private Long commentId;
    private String authorName;
    private String commentText;

    public CommentDetailDto(final Long commentId, final String authorName, final String commentText) {
        this.commentId = commentId;
        this.authorName = authorName;
        this.commentText = commentText;
    }

    public CommentDetailDto() {
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(final Long commentId) {
        this.commentId = commentId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(final String authorName) {
        this.authorName = authorName;
    }

    public String getCommentText() {
        return commentText;
    }

    public void setCommentText(final String commentText) {
        this.commentText = commentText;
    }
}

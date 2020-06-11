package kostka.moviecatalog.dto;

public class CommentDetailDto {
    private Long commentId;
    private String authorName;
    private String commentText;
    private Long userId;

    public CommentDetailDto(final Long commentId,
                            final String authorName,
                            final String commentText,
                            final Long userId) {
        this.commentId = commentId;
        this.authorName = authorName;
        this.commentText = commentText;
        this.userId = userId;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(final Long userId) {
        this.userId = userId;
    }
}

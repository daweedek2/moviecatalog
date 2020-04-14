package kostka.commentservice.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException() {
        super("Comment does not exist.");
    }
}

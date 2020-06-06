package kostka.moviecatalog.exception;

public class UserNotAllowedToBuyMovieException extends RuntimeException {
    public UserNotAllowedToBuyMovieException(final String message) {
        super("User is not allowed to buy movie, because " + message);
    }
}

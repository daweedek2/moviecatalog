package kostka.moviecatalog.exception;

public class UserNotAllowedToBuyMovieException extends RuntimeException {
    public UserNotAllowedToBuyMovieException() {
        super("User is not allowed to buy movie");
    }
}

package kostka.moviecatalog.exception;

public class UserNameNotUniqueException extends RuntimeException {
    public UserNameNotUniqueException() {
        super("User name is already used.");
    }
}

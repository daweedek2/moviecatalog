package kostka.moviecatalog.exception;

public class MovieNotFoundException extends RuntimeException {
    public MovieNotFoundException() {
        super("Movie not exists.");
    }
}

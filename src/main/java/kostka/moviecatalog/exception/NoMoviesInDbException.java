package kostka.moviecatalog.exception;

public class NoMoviesInDbException extends RuntimeException {
    public NoMoviesInDbException() {
        super("No movie is present in the DB!");
    }
}

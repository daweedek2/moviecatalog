package kostka.moviecatalog.exception;

public class FutureBirthDateException extends RuntimeException {
    public FutureBirthDateException() {
        super("BirthDate is in future.");
    }
}

package moviecatalog.shopservice.exception;

public class InvalidDtoException extends RuntimeException {
    public InvalidDtoException() {
        super("Invalid Dto");
    }
}

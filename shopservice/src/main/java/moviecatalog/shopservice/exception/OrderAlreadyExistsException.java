package moviecatalog.shopservice.exception;

public class OrderAlreadyExistsException extends RuntimeException {
    public OrderAlreadyExistsException() {
        super("User already bought movie.");
    }
}

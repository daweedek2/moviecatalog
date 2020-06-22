package kostka.moviecatalog.exception;

public class InvalidRuntimeConfigurationException extends RuntimeException {
    public InvalidRuntimeConfigurationException(final String message) {
        super("Runtime Configuration details are invalid: " + message);
    }
}

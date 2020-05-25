package kostka.moviecatalog.exception;

public class MissingRuntimeConfigurationException extends RuntimeException {
    public MissingRuntimeConfigurationException() {
        super("Runtime Configuration is not found.");
    }
}

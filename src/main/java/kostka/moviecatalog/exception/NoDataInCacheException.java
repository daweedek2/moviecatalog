package kostka.moviecatalog.exception;

public class NoDataInCacheException extends RuntimeException {
    public NoDataInCacheException() {
        super("There is no data in cache for provided key.");
    }
}

package info.smart_tools.smartactors.core.cached_collection.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.cached_collection.ICachedCollection} upsert method
 */
public class UpsertCacheItemException extends Exception {

    /**
     * Empty constructor for @GetCacheItemException
     */
    public UpsertCacheItemException() {
    }

    /**
     * @param message Message with exception
     */
    public UpsertCacheItemException(final String message) {
        super(message);
    }

    /**
     * @param message Message with exception
     * @param cause Cause of exception
     */
    public UpsertCacheItemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause Cause of exception
     */
    public UpsertCacheItemException(final Throwable cause) {
        super(cause);
    }
}

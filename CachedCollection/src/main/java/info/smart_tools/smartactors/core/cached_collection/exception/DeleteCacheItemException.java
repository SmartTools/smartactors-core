package info.smart_tools.smartactors.core.cached_collection.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.cached_collection.ICachedCollection} delete method
 */
public class DeleteCacheItemException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public DeleteCacheItemException(final String message) {
        super(message);
    }
    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public DeleteCacheItemException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

package info.smart_tools.smartactors.database.cached_collection.exception;

import info.smart_tools.smartactors.database.cached_collection.ICachedCollection;

/**
 * Exception for error in {@link ICachedCollection} getItem method
 */
public class GetCacheItemException extends Exception {

    /**
     * Empty constructor for @GetCacheItemException
     */
    public GetCacheItemException() {
    }

    /**
     * @param message Message with exception
     */
    public GetCacheItemException(final String message) {
        super(message);
    }

    /**
     * @param message Message with exception
     * @param cause Cause of exception
     */
    public GetCacheItemException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * @param cause Cause of exception
     */
    public GetCacheItemException(final Throwable cause) {
        super(cause);
    }
}

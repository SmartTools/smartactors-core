package info.smart_tools.smartactors.database.cached_collection.exception;

import info.smart_tools.smartactors.database.cached_collection.ICachedCollection;

/**
 * Exception for error in {@link ICachedCollection} delete method
 */
public class ClearCachedMapException extends Exception {

    /**
     * Empty constructor for @ClearCachedMapException
     */
    public ClearCachedMapException() {
    }

    /**
     * @param cause Cause of exception
     */
    public ClearCachedMapException(final Throwable cause) {
        super(cause);
    }
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ClearCachedMapException(final String message) {
        super(message);
    }
    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public ClearCachedMapException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

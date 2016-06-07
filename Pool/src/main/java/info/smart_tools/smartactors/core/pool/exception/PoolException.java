package info.smart_tools.smartactors.core.pool.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.pool.Pool} methods
 */
public class PoolException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PoolException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PoolException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PoolException(final Throwable cause) {
        super(cause);
    }
}

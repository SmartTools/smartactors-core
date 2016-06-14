package info.smart_tools.smartactors.core.iscope.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.iscope.IScopeFactory} methods
 */
public class ScopeFactoryException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ScopeFactoryException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ScopeFactoryException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ScopeFactoryException(final Throwable cause) {
        super(cause);
    }
}

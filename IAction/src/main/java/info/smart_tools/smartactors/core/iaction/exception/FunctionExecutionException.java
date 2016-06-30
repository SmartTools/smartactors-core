package info.smart_tools.smartactors.core.iaction.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.iaction.IFunction} methods
 */
public class FunctionExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FunctionExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public FunctionExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FunctionExecutionException(final Throwable cause) {
        super(cause);
    }
}

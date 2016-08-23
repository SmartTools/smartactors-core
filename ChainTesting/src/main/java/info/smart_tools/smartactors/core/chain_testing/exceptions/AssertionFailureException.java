package info.smart_tools.smartactors.core.chain_testing.exceptions;

/**
 * Exception created by {@link info.smart_tools.smartactors.core.chain_testing.TestRunner} when assertion failed.
 */
public class AssertionFailureException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public AssertionFailureException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public AssertionFailureException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

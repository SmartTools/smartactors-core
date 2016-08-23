package info.smart_tools.smartactors.core.chain_testing.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.core.chain_testing.TestRunner} when given test description has invalid format.
 */
public class InvalidTestDescriptionException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public InvalidTestDescriptionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public InvalidTestDescriptionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

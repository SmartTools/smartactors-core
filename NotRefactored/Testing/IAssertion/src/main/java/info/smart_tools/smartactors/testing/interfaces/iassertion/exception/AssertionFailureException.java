package info.smart_tools.smartactors.testing.interfaces.iassertion.exception;

import info.smart_tools.smartactors.testing.interfaces.iassertion.IAssertion;

/**
 * Exception for error in {@link IAssertion} method
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

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public AssertionFailureException(final Throwable cause) {
        super(cause);
    }
}

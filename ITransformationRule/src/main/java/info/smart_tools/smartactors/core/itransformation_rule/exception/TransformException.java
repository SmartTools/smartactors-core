package info.smart_tools.smartactors.core.itransformation_rule.exception;

/**
 * Exception for transformation rules
 */
public class TransformException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public TransformException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public TransformException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public TransformException(final Throwable cause) {
        super(cause);
    }
}

package info.smart_tools.smartactors.core.iresponse_sender.exceptions;


/**
 * Exception for {@link info.smart_tools.smartactors.core.iresponse_sender.IResponseSender}
 */
public class ResponseSendingException extends Exception {
    /**
     * Default constructor
     */

    public ResponseSendingException() {
    }

    /**
     * Constructor with specific error message
     * @param message specific error message
     */

    public ResponseSendingException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ResponseSendingException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */

    public ResponseSendingException(final Throwable cause) {
        super(cause);
    }
}

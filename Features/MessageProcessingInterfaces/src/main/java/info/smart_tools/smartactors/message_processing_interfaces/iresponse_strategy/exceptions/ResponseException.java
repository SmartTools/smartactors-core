package info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy
 * IResponseStrategy} when an error occurs sending the response.
 */
public class ResponseException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ResponseException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ResponseException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ResponseException(final Throwable cause) {
        super(cause);
    }
}

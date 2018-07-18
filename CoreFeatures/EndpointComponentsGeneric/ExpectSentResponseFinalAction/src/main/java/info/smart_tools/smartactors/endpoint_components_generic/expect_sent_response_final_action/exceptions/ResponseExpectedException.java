package info.smart_tools.smartactors.endpoint_components_generic.expect_sent_response_final_action.exceptions;

public class ResponseExpectedException extends Exception {
    /**
     * Constructs a new exception with {@code null} as its detail message.
     */
    public ResponseExpectedException() {
        super();
    }

    /**
     * Constructs a new exception with the specified detail message.
     *
     * @param message the detail message.
     */
    public ResponseExpectedException(final String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified detail message and
     * cause.
     *
     * @param message the detail message * @param cause the cause
     */
    public ResponseExpectedException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new exception with the specified cause.
     *
     * @param cause the cause
     */
    public ResponseExpectedException(final Throwable cause) {
        super(cause);
    }
}

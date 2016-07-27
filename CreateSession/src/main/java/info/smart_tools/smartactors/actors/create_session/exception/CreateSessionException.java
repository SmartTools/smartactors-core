package info.smart_tools.smartactors.actors.create_session.exception;

/**
 * Exception for error in CreateSessionActor methods
 */
public class CreateSessionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public CreateSessionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public CreateSessionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public CreateSessionException(final Throwable cause) {
        super(cause);
    }
}

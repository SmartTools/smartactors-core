package info.smart_tools.smartactors.actors.check_user_by_email.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.actors.check_user_by_email.CheckUserByEmailActor} checkUser method
 */
public class NotFoundUserException extends Exception {
    /**
     * Constructor with specific error message
     * @param message specific error message
     */
    public NotFoundUserException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */
    public NotFoundUserException(final Throwable cause) {
        super(cause);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public NotFoundUserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

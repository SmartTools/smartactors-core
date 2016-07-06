package info.smart_tools.smartactors.actors.authentication.users.exceptions;

/**
 *
 */
public class AuthenticateUserException extends Exception {
    /**
     *
     * @param message
     */
    public AuthenticateUserException(final String message) {
        super(message);
    }

    /**
     *
     * @param message
     * @param cause
     */
    public AuthenticateUserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

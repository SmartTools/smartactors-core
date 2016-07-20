package info.smart_tools.smartactors.actors.authentication.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.actors.authentication}
 */
public class AuthFailException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public AuthFailException(final String message) {
        super(message);
    }
}

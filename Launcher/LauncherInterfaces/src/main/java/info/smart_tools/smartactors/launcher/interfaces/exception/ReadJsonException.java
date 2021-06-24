package info.smart_tools.smartactors.launcher.interfaces.exception;

public class ReadJsonException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ReadJsonException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ReadJsonException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ReadJsonException(final Throwable cause) {
        super(cause);
    }
}

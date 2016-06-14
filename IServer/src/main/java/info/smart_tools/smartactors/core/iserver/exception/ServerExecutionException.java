package info.smart_tools.smartactors.core.iserver.exception;

/**
 * Exception for error in {@link info.smart_tools.smartactors.core.iserver.IServer} start method
 */
public class ServerExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ServerExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ServerExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ServerExecutionException(final Throwable cause) {
        super(cause);
    }
}

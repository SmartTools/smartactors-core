package info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap;

/**
 * Exception thrown during process execution
 */
public class ProcessExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ProcessExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ProcessExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ProcessExecutionException(final Throwable cause) {
        super(cause);
    }
}

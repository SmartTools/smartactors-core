package info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap;

/**
 * Exception thrown on initialization of bootstrap wrapper
 */
public class BootstrapInitException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public BootstrapInitException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public BootstrapInitException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public BootstrapInitException(final Throwable cause) {
        super(cause);
    }
}

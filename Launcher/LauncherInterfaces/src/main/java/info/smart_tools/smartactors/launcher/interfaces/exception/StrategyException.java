package info.smart_tools.smartactors.launcher.interfaces.exception;

public class StrategyException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public StrategyException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public StrategyException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public StrategyException(final Throwable cause) {
        super(cause);
    }
}

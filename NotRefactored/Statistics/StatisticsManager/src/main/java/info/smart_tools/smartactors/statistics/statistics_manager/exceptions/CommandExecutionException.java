package info.smart_tools.smartactors.statistics.statistics_manager.exceptions;

/**
 * Exception thrown when error occurs executing statistics manager command.
 */
public class CommandExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public CommandExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public CommandExecutionException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public CommandExecutionException(final Throwable cause) {
        super(cause);
    }
}

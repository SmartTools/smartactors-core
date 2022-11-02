package info.smart_tools.smartactors.statistics.statistics_manager.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.statistics.statistics_manager.StatisticsManagerActor} when non-exist command
 * execution required.
 */
public class CommandNotFoundException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public CommandNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public CommandNotFoundException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public CommandNotFoundException(final Throwable cause) {
        super(cause);
    }
}

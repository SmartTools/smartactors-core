package info.smart_tools.smartactors.debugger.interfaces.exceptions;

/**
 * Exception thrown when execution of a debugger command is failed.
 */
public class CommandExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param cause    cause of this exception
     */
    public CommandExecutionException(final Throwable cause) {
        super(cause);
    }

    /**
     * The constructor.
     *
     * @param message    the message
     */
    public CommandExecutionException(final String message) {
        super(message);
    }
}

package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

/**
 * ExecutionException is a checked exception that wraps
 * an exception thrown by an invoked method.
 */
public class ExecutionException extends Exception {
    /**
     * Constructs a ExecutionException with detail message.
     *
     * @param message the detail message.
     */
    public ExecutionException(String message) {
        super(message);
    }

    /**
     * Constructs a ExecutionException with detail message and cause of an exception.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public ExecutionException(String message, Throwable cause) {
        super(message, cause);
    }

}

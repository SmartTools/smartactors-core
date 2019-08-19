package info.smart_tools.smartactors.morph_expressions.parser.exception;

/**
 * Signals of syntax error in an expression.
 */
public class SyntaxException extends RuntimeException {
    /**
     * Constructs a SyntaxException with a detail message.
     *
     * @param message the detail message.
     */
    public SyntaxException(final String message) {
        super(message);
    }

    /**
     * Constructs a SyntaxException with a detail message and a cause of an exception.
     *
     * @param message the detail message.
     * @param cause the cause of the exception.
     */
    public SyntaxException(final String message, final Throwable cause) {
        super(message.isEmpty() ? cause.toString() : message, cause);
    }

}

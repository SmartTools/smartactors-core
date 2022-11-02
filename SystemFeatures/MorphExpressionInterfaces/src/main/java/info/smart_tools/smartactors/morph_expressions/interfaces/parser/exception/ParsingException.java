package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

/**
 * Signals of parsing error.
 */
public class ParsingException extends Exception {
    /**
     * Constructs a ParsingException with a detail message.
     *
     * @param message the detail message.
     */
    public ParsingException(String message) {
        super(message);
    }

    /**
     * Constructs a ParsingException with a detail message and a cause of an exception.
     *
     * @param message the detail message.
     * @param cause   the cause exception.
     */
    public ParsingException(String message, Throwable cause) {
        super(message.isEmpty() ? cause.toString() : message, cause);
    }

}

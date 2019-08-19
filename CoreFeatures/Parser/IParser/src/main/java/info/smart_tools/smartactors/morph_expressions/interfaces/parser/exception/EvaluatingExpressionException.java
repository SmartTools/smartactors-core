package info.smart_tools.smartactors.morph_expressions.interfaces.parser.exception;

/**
 * Signal of evaluation by expression error.
 */
public class EvaluatingExpressionException extends Exception {
    /**
     * Constructs a RuleException with a detail message.
     *
     * @param message the detail message.
     */
    public EvaluatingExpressionException(final String message) {
        super(message);
    }

    /**
     * Constructs a RuleException with a detail message and a cause of an exception.
     *
     * @param message the detail message.
     * @param cause   the cause of the exception.
     */
    public EvaluatingExpressionException(final String message, final Throwable cause) {
        super(message.isEmpty() ? cause.toString() : message, cause);
    }

}

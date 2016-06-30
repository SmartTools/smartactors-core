package info.smart_tools.smartactors.core.imulti_action_strategy.exception;

/**
 * Exception that occurs when instance of {@link info.smart_tools.smartactors.core.imulti_action_strategy.IMultiActionStrategy}
 * doesn't contains specified {@link info.smart_tools.smartactors.core.iaction.IFunction}
 */
public class FunctionNotFoundException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public FunctionNotFoundException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public FunctionNotFoundException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public FunctionNotFoundException(final Throwable cause) {
        super(cause);
    }
}
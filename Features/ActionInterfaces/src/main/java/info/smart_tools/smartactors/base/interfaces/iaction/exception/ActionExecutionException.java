package info.smart_tools.smartactors.base.interfaces.iaction.exception;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;

/**
 * Exception for error in {@link IAction} methods
 */
public class ActionExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ActionExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ActionExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ActionExecutionException(final Throwable cause) {
        super(cause);
    }
}

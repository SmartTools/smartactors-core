package info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.exception;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;

/**
 * Exception for error in {@link IBootstrapItem} undo method
 */
public class RevertProcessExecutionException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public RevertProcessExecutionException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public RevertProcessExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public RevertProcessExecutionException(final Throwable cause) {
        super(cause);
    }
}

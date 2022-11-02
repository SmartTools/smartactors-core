package info.smart_tools.smartactors.ioc.istrategy_container.exception;

import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;

/**
 * Exception for error in {@link IStrategyContainer} methods
 */
public class StrategyContainerException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public StrategyContainerException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public StrategyContainerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public StrategyContainerException(final Throwable cause) {
        super(cause);
    }
}

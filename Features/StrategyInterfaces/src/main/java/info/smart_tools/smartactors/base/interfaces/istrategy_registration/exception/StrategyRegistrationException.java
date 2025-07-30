package info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;

/**
 * Exception for error in {@link IStrategy} methods
 */
public class StrategyRegistrationException extends Exception {

    /**
     * Constructor with specific error message as argument
     *
     * @param message specific error message
     */
    public StrategyRegistrationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */

    public StrategyRegistrationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     *
     * @param cause specific cause
     */
    public StrategyRegistrationException(final Throwable cause) {
        super(cause);
    }
}

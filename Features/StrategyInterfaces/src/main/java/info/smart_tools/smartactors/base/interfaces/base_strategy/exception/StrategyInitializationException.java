package info.smart_tools.smartactors.base.interfaces.base_strategy.exception;

import info.smart_tools.smartactors.base.interfaces.base_strategy.BaseStrategy;

/**
 * Exception thrown by {@link BaseStrategy}
 */
// TODO: should be regular exception, not RuntimeException
public class StrategyInitializationException extends Exception {

    /**
     * Throw exception with the message
     *
     * @param message exception message
     */
    public StrategyInitializationException(final String message) {
        super(message);
    }

    /**
     * Throw exception with the message and the cause of the exception
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public StrategyInitializationException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

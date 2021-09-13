package info.smart_tools.simpleactors.commons.exception;

import info.smart_tools.simpleactors.SimpleActorsStarter;

/**
 * Exception thrown by {@link SimpleActorsStarter}
 */
public class SimpleActorsStartException extends Exception {

    /**
     * Throw exception with the message
     *
     * @param message exception message
     */
    public SimpleActorsStartException(final String message) {
        super(message);
    }

    /**
     * Throw exception with the message and the cause of the exception
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public SimpleActorsStartException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

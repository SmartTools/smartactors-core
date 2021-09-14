package info.smart_tools.simpleactors.commons.exception;

import info.smart_tools.simpleactors.CommandProcessor;

/**
 * Exception thrown by {@link CommandProcessor}
 */
public class ProcessExecutionException extends Exception {

    /**
     * Throw exception with the message
     *
     * @param message exception message
     */
    public ProcessExecutionException(final String message) {
        super(message);
    }

    /**
     * Throw exception with the message and the cause of the exception
     *
     * @param message exception message
     * @param cause   exception cause
     */
    public ProcessExecutionException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

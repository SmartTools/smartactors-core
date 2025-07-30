package info.smart_tools.smartactors.checkpoint.interfaces.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.checkpoint.interfaces.IRecoverStrategy recover strategy} when it cannot be
 * executed.
 */
public class RecoverStrategyExecutionException extends Exception {
    /**
     * The constructor.
     *
     * @param msg      the message
     * @param cause    the cause
     */
    public RecoverStrategyExecutionException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}

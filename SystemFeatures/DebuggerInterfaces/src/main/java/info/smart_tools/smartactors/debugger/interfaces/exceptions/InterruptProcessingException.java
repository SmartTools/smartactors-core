package info.smart_tools.smartactors.debugger.interfaces.exceptions;

/**
 * Exception thrown by debugger when error occurs processing interrupt.
 */
public class InterruptProcessingException extends Exception {
    /**
     * The constructor.
     *
     * @param msg the message
     * @param cause the cause
     */
    public InterruptProcessingException(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}

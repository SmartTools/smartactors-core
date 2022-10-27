package info.smart_tools.smartactors.debugger.interfaces.exceptions;

import info.smart_tools.smartactors.debugger.interfaces.IDebuggerBreakpointsStorage;

/**
 * Exception thrown by {@link IDebuggerBreakpointsStorage}.
 */
public class BreakpointStorageException extends Exception {
    /**
     * The constructor.
     *
     * @param message    the message
     * @param cause      the cause
     */
    public BreakpointStorageException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

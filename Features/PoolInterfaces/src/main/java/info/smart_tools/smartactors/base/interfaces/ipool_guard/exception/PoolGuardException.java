package info.smart_tools.smartactors.base.interfaces.ipool_guard.exception;

import info.smart_tools.smartactors.base.interfaces.ipool_guard.IPoolGuard;

/**
 * Exception for error in {@link IPoolGuard} methods
 */
public class PoolGuardException extends Exception {

    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public PoolGuardException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public PoolGuardException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public PoolGuardException(final Throwable cause) {
        super(cause);
    }
}
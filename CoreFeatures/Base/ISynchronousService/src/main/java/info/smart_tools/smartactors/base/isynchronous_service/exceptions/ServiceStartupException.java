package info.smart_tools.smartactors.base.isynchronous_service.exceptions;

/**
 * Exception thrown by {@link info.smart_tools.smartactors.base.isynchronous_service.ISynchronousService#start() service start} method when
 * the service cannot be started.
 */
public class ServiceStartupException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ServiceStartupException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ServiceStartupException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ServiceStartupException(final Throwable cause) {
        super(cause);
    }
}

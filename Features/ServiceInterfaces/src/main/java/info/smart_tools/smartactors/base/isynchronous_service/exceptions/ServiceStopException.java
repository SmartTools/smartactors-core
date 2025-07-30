package info.smart_tools.smartactors.base.isynchronous_service.exceptions;

import info.smart_tools.smartactors.base.isynchronous_service.ISynchronousService;

/**
 * Exception thrown by {@link ISynchronousService#stop() service stop} method when the service cannot be stopped.
 */
public class ServiceStopException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public ServiceStopException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public ServiceStopException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public ServiceStopException(final Throwable cause) {
        super(cause);
    }
}

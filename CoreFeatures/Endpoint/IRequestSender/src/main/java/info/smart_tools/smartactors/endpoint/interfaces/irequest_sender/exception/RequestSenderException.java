package info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception;

import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.IRequestSender;

/**
 * Exception for {@link IRequestSender}
 */
public class RequestSenderException extends Exception {
    public RequestSenderException(final String message) {
        super(message);
    }

    public RequestSenderException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestSenderException(final Throwable cause) {
        super(cause);
    }
}

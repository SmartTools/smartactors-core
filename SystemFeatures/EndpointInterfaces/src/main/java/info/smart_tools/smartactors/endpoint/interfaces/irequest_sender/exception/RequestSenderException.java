package info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.exception;

import info.smart_tools.smartactors.endpoint.interfaces.irequest_sender.IRequestSender;

/**
 * Exception for {@link IRequestSender}
 */
public class RequestSenderException extends Exception {
    public RequestSenderException(String message) {
        super(message);
    }

    public RequestSenderException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestSenderException(Throwable cause) {
        super(cause);
    }
}

package info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.exception;

import info.smart_tools.smartactors.endpoint.interfaces.iresponse_handler.IResponseHandler;

/**
 * Exception for {@link IResponseHandler}
 */
public class ResponseHandlerException extends Exception {

    public ResponseHandlerException(final String message) {
        super(message);
    }

    public ResponseHandlerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ResponseHandlerException(final Throwable cause) {
        super(cause);
    }
}

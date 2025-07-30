package info.smart_tools.smartactors.endpoint.irequest_maker.exception;

/**
 * Created by sevenbits on 14.10.16.
 */
public class RequestMakerException extends Exception {
    public RequestMakerException(final String message) {
        super(message);
    }

    public RequestMakerException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestMakerException(final Throwable cause) {
        super(cause);
    }
}

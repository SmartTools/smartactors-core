package info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.exception;

/**
 * Created by sevenbits on 15.10.16.
 */
public class HttpsRequestSenderActorException extends Exception {
    public HttpsRequestSenderActorException(final String message) {
        super(message);
    }

    public HttpsRequestSenderActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public HttpsRequestSenderActorException(final Throwable cause) {
        super(cause);
    }
}

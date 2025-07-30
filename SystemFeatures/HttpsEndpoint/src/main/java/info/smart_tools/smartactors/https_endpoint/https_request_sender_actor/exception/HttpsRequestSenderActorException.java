package info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.exception;

/**
 * Created by sevenbits on 15.10.16.
 */
public class HttpsRequestSenderActorException extends Exception {
    public HttpsRequestSenderActorException(String message) {
        super(message);
    }

    public HttpsRequestSenderActorException(String message, Throwable cause) {
        super(message, cause);
    }

    public HttpsRequestSenderActorException(Throwable cause) {
        super(cause);
    }
}

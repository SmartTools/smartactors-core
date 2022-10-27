package info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.exception;

/**
 * Exception for {@link info.smart_tools.smartactors.http_endpoint.http_request_sender_actor.HttpRequestSenderActor}
 */
public class HttpRequestSenderActorException extends Exception {
    /**
     * Constructor with specific error message as argument
     *
     * @param message specific error message
     */
    public HttpRequestSenderActorException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     *
     * @param message specific error message
     * @param cause   specific cause
     */
    public HttpRequestSenderActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as arguments
     *
     * @param cause specific cause
     */
    public HttpRequestSenderActorException(final Throwable cause) {
        super(cause);
    }
}

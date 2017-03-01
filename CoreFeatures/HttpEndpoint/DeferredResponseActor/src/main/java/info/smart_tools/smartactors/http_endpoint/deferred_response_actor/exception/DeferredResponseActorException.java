package info.smart_tools.smartactors.http_endpoint.deferred_response_actor.exception;

/**
 * Exception for errors in {@link info.smart_tools.smartactors.http_endpoint.deferred_response_actor.DeferredResponseActor}.
 */
public class DeferredResponseActorException extends Exception {

    /**
     * Constructor with specific message and cause
     * @param message the message
     */
    public DeferredResponseActorException(String message) {
        super(message);
    }

    /**
     * Constructor with specific message and cause
     * @param message the message
     * @param cause the cause
     */
    public DeferredResponseActorException(String message, Exception cause) {
        super(message, cause);
    }

}

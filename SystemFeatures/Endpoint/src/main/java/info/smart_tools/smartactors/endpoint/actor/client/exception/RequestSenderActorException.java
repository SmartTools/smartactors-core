package info.smart_tools.smartactors.endpoint.actor.client.exception;

import info.smart_tools.smartactors.endpoint.actor.client.ClientActor;

/**
 * Exception for {@link ClientActor}
 */
public class RequestSenderActorException extends Exception {

    public RequestSenderActorException(String message) {
        super(message);
    }

    public RequestSenderActorException(String message, Throwable cause) {
        super(message, cause);
    }

    public RequestSenderActorException(Throwable cause) {
        super(cause);
    }
}

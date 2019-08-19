package info.smart_tools.smartactors.endpoint.actor.client.exception;

import info.smart_tools.smartactors.endpoint.actor.client.ClientActor;

/**
 * Exception for {@link ClientActor}
 */
public class RequestSenderActorException extends Exception {

    public RequestSenderActorException(final String message) {
        super(message);
    }

    public RequestSenderActorException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public RequestSenderActorException(final Throwable cause) {
        super(cause);
    }
}

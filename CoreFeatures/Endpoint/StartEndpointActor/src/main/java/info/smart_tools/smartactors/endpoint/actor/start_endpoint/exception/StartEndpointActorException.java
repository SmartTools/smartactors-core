package info.smart_tools.smartactors.endpoint.actor.start_endpoint.exception;

/**
 * Created by sevenbits on 12.11.16.
 */
public class StartEndpointActorException extends Exception {
    public StartEndpointActorException(String message) {
        super(message);
    }

    public StartEndpointActorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StartEndpointActorException(Throwable cause) {
        super(cause);
    }
}

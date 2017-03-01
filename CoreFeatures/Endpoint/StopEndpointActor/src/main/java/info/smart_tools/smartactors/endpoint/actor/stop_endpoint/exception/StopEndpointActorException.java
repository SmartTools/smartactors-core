package info.smart_tools.smartactors.endpoint.actor.stop_endpoint.exception;

/**
 * Created by sevenbits on 12.11.16.
 */
public class StopEndpointActorException extends Exception {
    public StopEndpointActorException(String message) {
        super(message);
    }

    public StopEndpointActorException(String message, Throwable cause) {
        super(message, cause);
    }

    public StopEndpointActorException(Throwable cause) {
        super(cause);
    }
}

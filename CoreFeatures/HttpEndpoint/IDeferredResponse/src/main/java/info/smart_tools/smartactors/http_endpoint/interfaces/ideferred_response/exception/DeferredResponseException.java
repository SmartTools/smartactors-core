package info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.exception;

/**
 * Exception for errors in implements {@link info.smart_tools.smartactors.http_endpoint.interfaces.ideferred_response.IDeferredResponse}.
 */
public class DeferredResponseException extends Exception {

    /**
     * Constructor with specific message and cause
     * @param message the message
     */
    public DeferredResponseException(String message) {
        super(message);
    }

    /**
     * Constructor with specific message and cause
     * @param message the message
     * @param cause the cause
     */
    public DeferredResponseException(String message, Exception cause) {
        super(message, cause);
    }
}

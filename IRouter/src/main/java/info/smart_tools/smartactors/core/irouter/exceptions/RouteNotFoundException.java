package info.smart_tools.smartactors.core.irouter.exceptions;

/**
 * Exception thrown by a router when receiver for given id is not found.
 */
public class RouteNotFoundException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public RouteNotFoundException(final String message) {
        super(message);
    }
}

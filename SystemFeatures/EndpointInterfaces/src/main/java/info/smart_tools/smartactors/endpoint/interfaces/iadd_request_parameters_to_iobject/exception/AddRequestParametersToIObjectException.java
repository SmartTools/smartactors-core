package info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.exception;

import info.smart_tools.smartactors.endpoint.interfaces.iadd_request_parameters_to_iobject.IAddRequestParametersToIObject;

/**
 * Exception class for {@link IAddRequestParametersToIObject}
 */
public class AddRequestParametersToIObjectException extends Exception {

    /**
     * Constructor with specific error message
     * @param message specific error message
     */

    public AddRequestParametersToIObjectException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */
    public AddRequestParametersToIObjectException(final String message, final Throwable cause) {
        super(message, cause);
    }


    /**
     * Constructor with specific cause as arguments
     * @param cause specific cause
     */
    public AddRequestParametersToIObjectException(final Throwable cause) {
        super(cause);
    }
}

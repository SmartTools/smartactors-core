package info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions;

import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.IRoutedObjectCreator;

/**
 * Exception thrown by {@link IRoutedObjectCreator} when it fails to create a
 * object.
 */
public class ObjectCreationException extends Exception {
    /**
     * Constructor with specific error message as argument
     * @param message specific error message
     */
    public ObjectCreationException(final String message) {
        super(message);
    }

    /**
     * Constructor with specific error message and specific cause as arguments
     * @param message specific error message
     * @param cause specific cause
     */

    public ObjectCreationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructor with specific cause as argument
     * @param cause specific cause
     */
    public ObjectCreationException(final Throwable cause) {
        super(cause);
    }
}

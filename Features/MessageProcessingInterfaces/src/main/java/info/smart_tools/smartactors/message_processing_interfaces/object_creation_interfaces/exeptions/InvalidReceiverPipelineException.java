package info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions;

import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;

/**
 * Exception thrown when any of components detects that pipeline of receiver creators is not valid.
 *
 * Some reasons of pipeline to be invalid:
 * <ul>
 *     <li>Listener expects the object to be a receiver, but the object is not.</li>
 *     <li>Listener expects at least one object but {@link IReceiverObjectListener#endItems()} is called after 0 calls to {@link
 *     IReceiverObjectListener#acceptItem(Object, Object)}</li>
 *     <li>Listener expects a object to have a defined identifier but the object does not (the step responsible for identifier definition is
 *     missing in pipeline).</li>
 * </ul>
 */
public class InvalidReceiverPipelineException extends Exception {
    /**
     * The constructor.
     *
     * @param message       the message
     * @param cause         the cause
     */
    public InvalidReceiverPipelineException(final String message, final Throwable cause) {
        super(message, cause);
    }

    /**
     * The constructor.
     *
     * @param message       the message
     */
    public InvalidReceiverPipelineException(final String message) {
        super(message);
    }

    /**
     * The constructor.
     *
     * @param cause         the cause
     */
    public InvalidReceiverPipelineException(final Throwable cause) {
        super(cause);
    }
}

package info.smart_tools.smartactors.message_processing_interfaces.message_processing;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;

/**
 * Interface for a message receiver - object that can receive messages.
 */
public interface IMessageReceiver {
    /**
     * Receive the message and execute given action when done.
     *
     * Passes exception to the action if any error occurs while processing the message.
     *
     * @param processor the {@link IMessageProcessor} processing the message
     * @throws MessageReceiveException if error occurs receiving the message
     * @throws AsynchronousOperationException if error occurs starting asynchronous operation
     */
    void receive(IMessageProcessor processor) throws MessageReceiveException, AsynchronousOperationException;

    /**
     * Dispose the receiver.
     *
     * This method have to unregister all registrations in IOC done during receiver creation.
     * Passes exception if any error occurs while disposing the receiver.
     */
    void dispose();
}

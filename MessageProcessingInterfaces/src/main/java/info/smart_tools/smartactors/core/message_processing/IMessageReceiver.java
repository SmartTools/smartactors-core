package info.smart_tools.smartactors.core.message_processing;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

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
     * @param arguments the arguments object passed to the receiver from the {@link IReceiverChain}
     * @param onEnd the action that should be executed when message processing by this receiver is completed
     * @throws MessageReceiveException if error occurs receiving the message
     */
    void receive(IMessageProcessor processor, IObject arguments, IAction<Throwable> onEnd) throws MessageReceiveException;
}

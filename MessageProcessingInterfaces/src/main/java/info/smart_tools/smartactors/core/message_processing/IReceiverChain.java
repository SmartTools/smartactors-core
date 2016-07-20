package info.smart_tools.smartactors.core.message_processing;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iobject.IObject;

/**
 * Chain of message receivers.
 */
public interface IReceiverChain {
    /**
     * Get {@link IMessageReceiver} at given index.
     *
     * @param index    index of the receiver in the chain
     * @return receiver at given index or {@code null} if there is no such receiver
     */
    IMessageReceiver get(int index);

    /**
     * Get arguments object that should be passed to the receiver at given index of this chain.
     *
     * @param index    index of the receiver in the chain
     * @return arguments that should be passed to the receiver
     * @see IMessageReceiver#receive(IMessageProcessor, IObject, IAction)
     */
    IObject getArguments(int index);

    /**
     * Get name of this chain.
     *
     * @return name of this chain
     */
    String getName();

    /**
     * Get the chin of receivers that has to be executed if exception occurs during execution of this chain.
     *
     * @param exception the exception occurred
     * @return the chin of receivers that has to be executed if exception occurs during execution of this chain or
     *         {@code null} if no such chain defined for this one
     */
    IReceiverChain getExceptionalChain(Throwable exception);
}

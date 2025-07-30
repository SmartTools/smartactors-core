package info.smart_tools.smartactors.message_processing_interfaces.message_processing;

import info.smart_tools.smartactors.iobject.iobject.IObject;

import java.util.Collection;

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
     * @see IMessageReceiver#receive(IMessageProcessor)
     */
    IObject getArguments(int index);

    /**
     * Get Id of this chain.
     *
     * @return Id of this chain
     */
    Object getId();

    /**
     * Get name of this chain.
     *
     * @return name of this chain
     */
    Object getName();

    /**
     * Get the chain of receivers (and environments) that has to be executed if exception occurs during execution of this chain.
     *
     * @param exception the exception occurred
     * @return the chain of receivers (and environments) that has to be executed if exception occurs during execution of this chain or
     *         {@code null} if no such chain defined for this one
     */
    IObject getExceptionalChainNamesAndEnvironments(Throwable exception);

    /**
     * Get list of all exceptional chain used y this chain.
     *
     * <p>
     *     This method may be used for serialization of chains to create clones of exceptional chains on deserialization.
     * </p>
     *
     * @return list of all exceptional chains (names) used by this one
     */
    Collection<Object> getExceptionalChainNames();

    /**
     * Get chain description
     * @return the description of chain
     */
    IObject getChainDescription();
}

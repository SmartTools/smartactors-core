package info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;

/**
 * Interface for MessageBus
 */
public interface IMessageBusContainer {

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    IKey getMessageBusKey();

    /**
     * Send message to the default chain.
     *
     * @param message the message for send
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    void send(IObject message, boolean scopeSwitching) throws SendingMessageException;

    /**
     * Send message to the specific chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain to send to
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    void send(IObject message, Object chainName, boolean scopeSwitching) throws SendingMessageException;

    /**
     * Send message to the default chain sending response to the other chain.
     *
     * @param message the message for send
     * @param replyToChainName the name of specific chain to reply to
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    void sendAndReply(IObject message, Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException;

    /**
     * Send message to the specific chain sending response to the other chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain to send to
     * @param replyToChainName the name of specific chain to reply to
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    void sendAndReply(IObject message, Object chainName, Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException;
}

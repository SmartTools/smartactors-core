package info.smart_tools.smartactors.message_bus.message_bus;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.IMessageBusContainer;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.message_bus_container_with_scope.MessageBusContainer;

/**
 * Realization of MessageBus by ServiceLocator pattern
 */
public final class MessageBus {

    private static IMessageBusContainer container;

    /**
     * Initialize MessageBus by default implementation of {@link IMessageBusContainer}
     */
    static {
        container = new MessageBusContainer();
    }

    /**
     * Default private constructor
     */
    private MessageBus() {
    }

    /**
     * Return specific instance of {@link IKey} for container ID
     * @return instance of {@link IKey}
     */
    public static IKey getMessageBusKey() {
        return container.getMessageBusKey();
    }

    /**
     * Send message to the default chain.
     *
     * @param message the sending message
     * @throws SendingMessageException if sending of message has been failed
     */
    public static void send(final IObject message)
            throws SendingMessageException {
        container.send(message, true);
    }

    /**
     * Send message to the specific chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain
     * @throws SendingMessageException if message sending has been failed
     */
    public static void send(final IObject message, final Object chainName)
            throws SendingMessageException {
        container.send(message, chainName, true);
    }

    /**
     * Send message to the default chain sending response to the other chain.
     * @param message the message for send
     * @param replyToChainName the name of specific chain to reply to
     * @throws SendingMessageException if message sending has been failed
     */
    public static void sendAndReply(final IObject message, final Object replyToChainName)
            throws SendingMessageException {
        container.sendAndReply(message, replyToChainName, true);
    }

    /**
     * Send message to the specific chain sending response to the other chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain to send to
     * @param replyToChainName the name of specific chain to reply to
     * @throws SendingMessageException if message sending has been failed
     */
    public static void sendAndReply(final IObject message, final Object chainName, final Object replyToChainName)
            throws SendingMessageException {
        container.sendAndReply(message, chainName, replyToChainName, true);
    }

    /**
     * Send message to the default chain.
     *
     * @param message the sending message
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if sending of message has been failed
     */
    public static void send(final IObject message, boolean scopeSwitching)
            throws SendingMessageException {
        container.send(message, scopeSwitching);
    }

    /**
     * Send message to the specific chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    public static void send(final IObject message, final Object chainName, boolean scopeSwitching)
            throws SendingMessageException {
        container.send(message, chainName, scopeSwitching);
    }

    /**
     * Send message to the default chain sending response to the other chain.
     * @param message the message for send
     * @param replyToChainName the name of specific chain to reply to
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    public static void sendAndReply(final IObject message, final Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException {
        container.sendAndReply(message, replyToChainName, scopeSwitching);
    }

    /**
     * Send message to the specific chain sending response to the other chain.
     *
     * @param message the message for send
     * @param chainName the name of specific chain to send to
     * @param replyToChainName the name of specific chain to reply to
     * @param scopeSwitching if false then scope is not changed on chain call
     * @throws SendingMessageException if message sending has been failed
     */
    public static void sendAndReply(final IObject message, final Object chainName, final Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException {
        container.sendAndReply(message, chainName, replyToChainName, scopeSwitching);
    }
}

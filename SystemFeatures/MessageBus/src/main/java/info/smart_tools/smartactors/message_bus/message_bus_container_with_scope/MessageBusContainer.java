package info.smart_tools.smartactors.message_bus.message_bus_container_with_scope;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.IMessageBusContainer;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.scope.iscope.exception.ScopeException;
import info.smart_tools.smartactors.scope.iscope_provider_container.exception.ScopeProviderException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;

/**
 * Implementation of {@link IMessageBusContainer}.
 */
public class MessageBusContainer implements IMessageBusContainer {

    /**
     * Key for getting instance of {@link IMessageBusContainer} from current scope
     */
    private IKey messageBusContainerKey;

    /**
     * Default constructor
     */
    public MessageBusContainer() {
        try {
            this.messageBusContainerKey = new Key(java.util.UUID.randomUUID().toString());
        } catch (Exception e) {
            throw new RuntimeException("Initialization of Message bus container has been failed.", e);
        }
    }

    @Override
    public IKey getMessageBusKey() {
        return this.messageBusContainerKey;
    }

    @Override
    public void send(final IObject message, boolean scopeSwitching) throws SendingMessageException {
        try {
            final IMessageBusHandler handler = (IMessageBusHandler) ScopeProvider
                    .getCurrentScope()
                    .getValue(messageBusContainerKey);
            handler.handle(message, scopeSwitching);
        } catch (ScopeException e) {
            throw new SendingMessageException("Could not get MessageBusContainer with key: " + messageBusContainerKey + " from the current scope.", e);
        } catch (ScopeProviderException e) {
            throw new SendingMessageException("Could not get current scope.", e);
        } catch (Throwable e) {
            throw new SendingMessageException("Could not send message.", e);
        }
    }

    @Override
    public void send(final IObject message, final Object chainName, boolean scopeSwitching)
            throws SendingMessageException {
        try {
            final IMessageBusHandler handler = (IMessageBusHandler) ScopeProvider
                    .getCurrentScope()
                    .getValue(messageBusContainerKey);
            handler.handle(message, chainName, scopeSwitching);
        } catch (ScopeException e) {
            throw new SendingMessageException("Could not get MessageBusContainer with key: " + messageBusContainerKey + " from the current scope.", e);
        } catch (ScopeProviderException e) {
            throw new SendingMessageException("Could not get current scope.", e);
        } catch (Throwable e) {
            throw new SendingMessageException("Could not send message to the chain " + chainName, e);
        }
    }

    @Override
    public void sendAndReply(final IObject message, final Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException {
        try {
            final IMessageBusHandler handler = (IMessageBusHandler) ScopeProvider
                    .getCurrentScope()
                    .getValue(messageBusContainerKey);
            handler.handleForReply(message, replyToChainName, scopeSwitching);
        } catch (ScopeException e) {
            throw new SendingMessageException("Could not get MessageBusContainer with key: " + messageBusContainerKey + " from the current scope.", e);
        } catch (ScopeProviderException e) {
            throw new SendingMessageException("Could not get current scope.", e);
        } catch (Throwable e) {
            throw new SendingMessageException("Could not send message with a reply to chain " + replyToChainName, e);
        }
    }

    @Override
    public void sendAndReply(final IObject message, final Object chainName, final Object replyToChainName, boolean scopeSwitching)
            throws SendingMessageException {
        try {
            final IMessageBusHandler handler = (IMessageBusHandler) ScopeProvider
                    .getCurrentScope()
                    .getValue(messageBusContainerKey);
            handler.handleForReply(message, chainName, replyToChainName, scopeSwitching);
        } catch (ScopeException e) {
            throw new SendingMessageException("Could not get MessageBusContainer with key: " + messageBusContainerKey + " from the current scope.", e);
        } catch (ScopeProviderException e) {
            throw new SendingMessageException("Could not get current scope.", e);
        } catch (Throwable e) {
            throw new SendingMessageException("Could not send message to the chain " + chainName + " with a reply to chain " + replyToChainName, e);
        }
    }
}

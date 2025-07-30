package info.smart_tools.smartactors.message_bus.message_bus_container_with_scope;

import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.IMessageBusContainer;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MessageBusContainer}
 */
public class MessageBusContainerTest extends IOCInitializer {

    private IStrategyContainer container = new StrategyContainer();

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Test
    public void checkCreation() {
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        assertNotNull(messageBusContainer);
        assertNotNull(messageBusContainer.getMessageBusKey());
    }

    @Test
    public void checkSendingMessage()
            throws Exception {
        IMessageBusHandler handler = mock(IMessageBusHandler.class);
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        ScopeProvider.getCurrentScope().setValue(messageBusContainer.getMessageBusKey(), handler);
        IObject message = mock(IObject.class);
        doNothing().when(handler).handle(message, true);

        messageBusContainer.send(message, true);
        verify(handler, times(1)).handle(message, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkSendingMessageException()
            throws Exception {
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        messageBusContainer.send(null, true);
        fail();
    }

    @Test
    public void checkSendingMessageWithSpecificChain()
            throws Exception {
        IMessageBusHandler handler = mock(IMessageBusHandler.class);
        Object chainName = mock(Object.class);
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        ScopeProvider.getCurrentScope().setValue(messageBusContainer.getMessageBusKey(), handler);
        IObject message = mock(IObject.class);
        doNothing().when(handler).handle(message, chainName, true);

        messageBusContainer.send(message, chainName, true);
        verify(handler, times(1)).handle(message, chainName, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkSendingMessageExceptionOnSendWithSpecificChain()
            throws Exception {
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        messageBusContainer.send(null, null, true);
        fail();
    }

    @Test
    public void checkSendingMessageWithReply()
            throws Exception {
        IMessageBusHandler handler = mock(IMessageBusHandler.class);
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        ScopeProvider.getCurrentScope().setValue(messageBusContainer.getMessageBusKey(), handler);
        IObject message = mock(IObject.class);
        Object chainNameForReply = mock(Object.class);
        doNothing().when(handler).handleForReply(message, chainNameForReply, true);

        messageBusContainer.sendAndReply(message, chainNameForReply, true);
        verify(handler, times(1)).handleForReply(message, chainNameForReply, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkSendingMessageExceptionOnSendingWithReply()
            throws Exception {
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        messageBusContainer.sendAndReply(null, null, true);
        fail();
    }

    @Test
    public void checkSendingMessageWithSpecificChainAndReply()
            throws Exception {
        IMessageBusHandler handler = mock(IMessageBusHandler.class);
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        ScopeProvider.getCurrentScope().setValue(messageBusContainer.getMessageBusKey(), handler);
        IObject message = mock(IObject.class);
        Object chainName = mock(Object.class);
        Object chainNameForReply = mock(Object.class);
        doNothing().when(handler).handleForReply(message, chainName, chainNameForReply, true);

        messageBusContainer.sendAndReply(message, chainName, chainNameForReply, true);
        verify(handler, times(1)).handleForReply(message, chainName, chainNameForReply, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkSendingMessageExceptionOnSendingWithSpecificChainAndReply()
            throws Exception {
        IMessageBusContainer messageBusContainer = new MessageBusContainer();
        messageBusContainer.sendAndReply(null, null, null, true);
        fail();
    }
}

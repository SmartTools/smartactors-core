package info.smart_tools.smartactors.message_bus.message_bus;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.IMessageBusContainer;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception.MessageBusHandlerException;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MessageBus}.
 */
public class MessageBusTest extends IOCInitializer {

    private IMessageBusHandler handler = mock(IMessageBusHandler.class);

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), this.handler);
    }

    @Test
    public void checkGettingMessageBusKey() {
        IKey key = MessageBus.getMessageBusKey();
        assertNotNull(key);
    }

    @Test
    public void checkSendingMessage()
            throws Exception {
        IObject message = mock(IObject.class);
        MessageBus.send(message);
        MessageBus.send(message, true);
        verify(this.handler, times(2)).handle(message, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkExceptionOnSendingMessage()
            throws Exception {
        doThrow(new MessageBusHandlerException("")).when(this.handler).handle(null, true);
        MessageBus.send(null);
        fail();
    }

    @Test
    public void checkSendingMessageWithSpecificChain()
            throws Exception {
        IObject message = mock(IObject.class);
        Object chainName = mock(Object.class);
        MessageBus.send(message, chainName);
        MessageBus.send(message, chainName, true);
        verify(this.handler, times(2)).handle(message, chainName, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkExceptionOnSendingMessageWithSpecificChain()
            throws Exception {
        doThrow(new MessageBusHandlerException("")).when(this.handler).handle(null, null, true);
        MessageBus.send(null, null);
        fail();
    }

    @Test
    public void checkSendingMessageWithReply()
            throws Exception {
        IObject message = mock(IObject.class);
        Object chainNameForReply = mock(Object.class);
        MessageBus.sendAndReply(message, chainNameForReply);
        MessageBus.sendAndReply(message, chainNameForReply, true);
        verify(this.handler, times(2)).handleForReply(message, chainNameForReply, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkExceptionOnSendingMessageWithReply()
            throws Exception {
        doThrow(new MessageBusHandlerException("")).when(this.handler).handleForReply(null, null, true);
        MessageBus.sendAndReply(null, null);
        fail();
    }

    @Test
    public void checkSendingMessageWithSpecificChainAndReply()
            throws Exception {
        IObject message = mock(IObject.class);
        Object chainName = mock(Object.class);
        Object chainNameForReply = mock(Object.class);
        MessageBus.sendAndReply(message, chainName, chainNameForReply);
        MessageBus.sendAndReply(message, chainName, chainNameForReply, true);
        verify(this.handler, times(2)).handleForReply(message, chainName, chainNameForReply, true);
    }

    @Test (expected = SendingMessageException.class)
    public void checkExceptionOnSendingMessageWithSpecificChainAndReply()
            throws Exception {
        doThrow(new MessageBusHandlerException("")).when(this.handler).handleForReply(null, null, null, true);
        MessageBus.sendAndReply(null, null, null);
        fail();
    }
}

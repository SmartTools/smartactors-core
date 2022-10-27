package info.smart_tools.smartactors.checkpoint.failure_action;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_container.exception.SendingMessageException;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.message_bus.MessageBus;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SendEnvelopeFailureAction}.
 */
public class SendEnvelopeFailureActionTest extends IOCInitializer {
    private IMessageBusHandler messageBusHandlerMock;
    private IAction<IObject> backupActionMock;
    private Object targetChainId = new Object();
    private IFieldName messageFN;
    private IObject messageMock = mock(IObject.class);

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        messageBusHandlerMock = mock(IMessageBusHandler.class);
        ScopeProvider.getCurrentScope().setValue(MessageBus.getMessageBusKey(), messageBusHandlerMock);

        backupActionMock = mock(IAction.class);

        messageFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "message___");
    }

    @Test
    public void Should_sendMessageToSpecifiedChain()
            throws Exception {
        IAction<IObject> action = new SendEnvelopeFailureAction(targetChainId, messageFN, backupActionMock);

        ArgumentCaptor<IObject> envelopeCaptor = ArgumentCaptor.forClass(IObject.class);

        action.execute(messageMock);

        verify(messageBusHandlerMock).handle(envelopeCaptor.capture(), same(targetChainId), eq(true));

        assertSame(messageMock, envelopeCaptor.getValue().getValue(messageFN));
    }

    @Test
    public void Should_callBackupActionWhenErrorOccursSendingTheEnvelope()
            throws Exception {
        doThrow(SendingMessageException.class).when(messageBusHandlerMock).handle(any(), any(), eq(true));

        IAction<IObject> action = new SendEnvelopeFailureAction(targetChainId, messageFN, backupActionMock);

        try {
            action.execute(messageMock);
            fail();
        } catch (ActionExecutionException ok) {
        }

        verify(backupActionMock).execute(same(messageMock));
    }

    @Test
    public void Should_suppressExceptionThrownByBackupAction()
            throws Exception {
        doThrow(SendingMessageException.class).when(messageBusHandlerMock).handle(any(), any(), eq(true));
        doThrow(ActionExecutionException.class).when(backupActionMock).execute(same(messageMock));

        IAction<IObject> action = new SendEnvelopeFailureAction(targetChainId, messageFN, backupActionMock);

        try {
            action.execute(messageMock);
            fail();
        } catch (ActionExecutionException ok) {
            assertTrue(ok.getCause().getSuppressed()[0] instanceof ActionExecutionException);
        }
    }
}

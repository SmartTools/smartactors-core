package info.smart_tools.smartactors.endpoint_components_generic.generic_exception_interceptor_message_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class GenericExceptionInterceptorMessageHandlerTest {
    private IMessageHandlerCallback messageHandlerCallbackMock;
    private IDefaultMessageContext messageContext;
    private IBiAction exceptionActionMock;
    private Object connectionContext = new Object();
    IMessageHandler messageHandler;

    @Before public void setUp() throws Exception {
        messageHandlerCallbackMock = mock(IMessageHandlerCallback.class);
        exceptionActionMock = mock(IBiAction.class);
        messageContext = new DefaultMessageContextImplementation();
        messageHandler = new GenericExceptionInterceptorMessageHandler(exceptionActionMock);
        messageContext.setConnectionContext(connectionContext);
    }

    @Test public void Should_doNothingWhenNoExceptionOccurs() throws Exception {
        messageHandler.handle(messageHandlerCallbackMock, messageContext);

        verify(messageHandlerCallbackMock).handle(same(messageContext));
    }

    @Test public void Should_callExceptionalActionWhenExceptionOccurs()
            throws Exception {
        doAnswer(invocationOnMock ->  {
            assertSame(messageContext, invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class));
            // Next handler may change (wrap/decorate/map) connection context before throwing exception
            messageContext.setConnectionContext(new Object());
            throw new MessageHandlerException("Whoops!!");
        }).when(messageHandlerCallbackMock).handle(any());

        try {
            messageHandler.handle(messageHandlerCallbackMock, messageContext);
            fail();
        } catch (MessageHandlerException e) {
            verify(exceptionActionMock).execute(same(connectionContext), same(e));
        }
    }
}

package info.smart_tools.smartactors.endpoint_components_generic.message_attribute_routing_message_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class MessageAttributeRoutingMessageHandlerTest {
    private IMessageHandlerCallback callback = mock(IMessageHandlerCallback.class);

    private IMessageHandler defaultHandler, specialHandler1, specialHandler2;

    private IMessageContext messageContext;
    private IFunction attributeExtractor;

    @Before public void setUp() throws Exception {
        defaultHandler = mock(IMessageHandler.class);
        specialHandler1 = mock(IMessageHandler.class);
        specialHandler2 = mock(IMessageHandler.class);
        messageContext = mock(IMessageContext.class);
        attributeExtractor = mock(IFunction.class);
    }

    @Test public void Should_routeToDefaultHandler() throws Exception {
        IMessageHandler handler = new MessageAttributeRoutingMessageHandler(attributeExtractor, new HashMap<>(), defaultHandler);

        when(attributeExtractor.execute(same(messageContext))).thenReturn("0");
        handler.handle(callback, messageContext);

        verify(defaultHandler).handle(same(callback), same(messageContext));
        verifyNoMoreInteractions(defaultHandler, specialHandler1, specialHandler2);
    }

    @Test public void Should_routeToSpecialHandlers() throws Exception {
        IMessageHandler handler = new MessageAttributeRoutingMessageHandler(attributeExtractor, new HashMap() {{
            put("1", specialHandler1);
            put("2", specialHandler2);
        }}, defaultHandler);

        when(attributeExtractor.execute(same(messageContext))).thenReturn("1");
        handler.handle(callback, messageContext);

        verify(specialHandler1).handle(same(callback), same(messageContext));
        verifyNoMoreInteractions(defaultHandler, specialHandler1, specialHandler2);
    }
}

package info.smart_tools.smartactors.endpoint_components_netty.outbound_netty_message_creation_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction;
import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import io.netty.handler.codec.http.FullHttpRequest;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class OutboundNettyMessageCreationHandlerTest {
    @Test public void Should_wrapCreatedMessageAndSetAsDestinationMessage() throws Exception {
        FullHttpRequest fullHttpRequest = mock(FullHttpRequest.class);
        IFunction0<FullHttpRequest> msgFactory = mock(IFunction0.class);
        when(msgFactory.execute()).thenReturn(fullHttpRequest).thenThrow(FunctionExecutionException.class);
        IDefaultMessageContext messageContext = new DefaultMessageContextImplementation();
        IMessageHandlerCallback callback = mock(IMessageHandlerCallback.class);

        new OutboundNettyMessageCreationHandler(msgFactory).handle(callback, messageContext);

        verify(callback).handle(same(messageContext));

        IOutboundMessageByteArray outboundMessageByteArray = (IOutboundMessageByteArray) messageContext.getDstMessage();

        assertTrue(outboundMessageByteArray instanceof OutboundNettyMessageByteArray);
        assertSame(fullHttpRequest, outboundMessageByteArray.getMessage());
    }
}

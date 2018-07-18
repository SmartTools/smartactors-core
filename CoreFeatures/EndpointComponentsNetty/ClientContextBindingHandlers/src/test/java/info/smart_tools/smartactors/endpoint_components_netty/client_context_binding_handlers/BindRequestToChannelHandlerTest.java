package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class BindRequestToChannelHandlerTest extends TrivialPluginsLoadingTestBase {
    private IObject request;
    private IDefaultMessageContext context;
    private IMessageHandlerCallback callback;
    private Channel channel;
    private Attribute channelAttribute;

    @Override
    protected void registerMocks() throws Exception {
        request = mock(IObject.class);
        context = mock(IDefaultMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);
        channel = mock(Channel.class);
        channelAttribute = mock(Attribute.class);

        when(context.getConnectionContext()).thenReturn(channel);
        when(context.getSrcMessage()).thenReturn(request);

        when(channel.attr(same(AttributeKeys.REQUEST_ATTRIBUTE_KEY))).thenReturn(channelAttribute);
    }

    @Test
    public void Should_storeRequestInChannelAttribute() throws Exception {
        doAnswer(i -> {
            verify(channelAttribute).set(same(request));
            verifyNoMoreInteractions(channelAttribute);
            return null;
        }).when(callback).handle(any());

        new BindRequestToChannelHandler<>().handle(callback, context);

        verify(callback).handle(same(context));
    }

    @Test
    public void Should_clearAttributeWhenNextThrows() throws Exception {
        doThrow(new MessageHandlerException()).when(callback).handle(any());

        try {
            new BindRequestToChannelHandler<>().handle(callback, context);
            fail();
        } catch (MessageHandlerException e) { }

        verify(channelAttribute).set(same(request));
        verify(channelAttribute).compareAndSet(same(request), isNull());
        verifyNoMoreInteractions(channelAttribute);
    }
}

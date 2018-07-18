package info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

public class ReleasePooledChannelHandlerTest extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext context;
    private Attribute channelAttribute;
    private Channel channel;
    private IMessageHandlerCallback callback;
    private ISocketConnectionPool pool;

    @Override
    protected void registerMocks() throws Exception {
        context = mock(IDefaultMessageContext.class);
        channel = mock(Channel.class);
        channelAttribute = mock(Attribute.class);
        callback = mock(IMessageHandlerCallback.class);
        pool = mock(ISocketConnectionPool.class);

        when(context.cast(IDefaultMessageContext.class)).thenReturn(context);
        when(context.getConnectionContext()).thenReturn(channel);
        when(channel.attr(same(AttributeKeys.POOL_ATTRIBUTE_KEY))).thenReturn(channelAttribute);
    }

    @Test
    public void Should_returnChannelToPoolWhenPoolAttributeIsSet() throws Exception {
        when(channelAttribute.getAndRemove()).thenReturn(pool).thenReturn(null);

        doAnswer(i -> {
            verify(pool).recycleChannel(same(channel));
            verify(context).setConnectionContext(isNull());
            return null;
        }).when(callback).handle(any());

        new ReleasePooledChannelHandler().handle(callback, context);

        verify(callback).handle(same(context));
    }

    @Test
    public void Should_closeChannelAndThrowWhenNoPoolAttributeIsSet() throws Exception {
        when(channelAttribute.getAndRemove()).thenReturn(null);

        try {
            new ReleasePooledChannelHandler().handle(callback, context);
            fail();
        } catch (MessageHandlerException ok) { }

        verify(callback, times(0)).handle(any());
        verify(channel).close();
    }
}

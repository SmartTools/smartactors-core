package info.smart_tools.smartactors.endpoint_components_netty.channel_pool_handlers;

import info.smart_tools.smartactors.endpoint_components_netty.isocket_connection_pool.ISocketConnectionPool;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import org.junit.Test;

import java.net.SocketAddress;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class AcquireChannelFromPoolHandlerTest extends TrivialPluginsLoadingTestBase {
    private Channel channel;
    private Attribute channelAttr;
    private SocketAddress address;
    private IObject env;
    private IDefaultMessageContext context;
    private IMessageHandlerCallback callback;
    private ISocketConnectionPool pool;

    @Override
    protected void registerMocks() throws Exception {
        address = mock(SocketAddress.class);
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        env.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "address"),
                address
        );

        context = mock(IDefaultMessageContext.class);
        when(context.cast(IDefaultMessageContext.class)).thenReturn(context);
        when(context.getSrcMessage()).thenReturn(env);

        callback = mock(IMessageHandlerCallback.class);

        channelAttr = mock(Attribute.class);
        channel = mock(Channel.class);
        when(channel.attr(same(AttributeKeys.POOL_ATTRIBUTE_KEY))).thenReturn(channelAttr);

        pool = mock(ISocketConnectionPool.class);
        when(pool.getChannel(any())).thenReturn(channel).thenThrow(AssertionError.class);
    }

    @Test
    public void Should_acquireChannelFromPool() throws Exception {
        doAnswer(i -> {
            verify(pool).getChannel(same(address));
            verify(channelAttr).set(same(pool));
            verify(context).setConnectionContext(same(channel));
            return null;
        }).when(callback).handle(any());

        new AcquireChannelFromPoolHandler<>(pool).handle(callback, context);

        verify(callback).handle(same(context));
    }
}

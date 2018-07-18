package info.smart_tools.smartactors.endpoint_components_netty.default_channel_initialization_handlers;

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

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link StoreOutboundChannelIdToContext}.
 */
public class StoreOutboundChannelIdToContextTest extends TrivialPluginsLoadingTestBase {
    @Test public void Should_storeAssociatedOutboundChannelIdInDstMessageContext() throws Exception {
        IDefaultMessageContext<Object, IObject, Channel> context
                = mock(IDefaultMessageContext.class);
        IObject env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), "{\"context\":{}}");
        Channel channel = mock(Channel.class);
        Attribute attribute = mock(Attribute.class);
        IMessageHandlerCallback<IDefaultMessageContext<Object, IObject, Channel>> callback
                = mock(IMessageHandlerCallback.class);

        when(context.getDstMessage()).thenReturn(env);
        when(context.getConnectionContext()).thenReturn(channel);
        when(channel.attr(same(ChannelAttributes.OUTBOUND_CHANNEL_ID_KEY))).thenReturn(attribute);
        when(attribute.get()).thenReturn("this-is-id");

        doAnswer(invocationOnMock -> {
            assertEquals(
                    "this-is-id",
                    ((IObject) env.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context")))
                            .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "channelId")));

            return null;
        }).when(callback).handle(same(context));

        new StoreOutboundChannelIdToContext<>().handle(callback, context);

        verify(callback).handle(same(context));
    }
}

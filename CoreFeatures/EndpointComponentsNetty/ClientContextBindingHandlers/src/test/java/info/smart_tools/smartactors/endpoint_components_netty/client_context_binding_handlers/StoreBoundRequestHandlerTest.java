package info.smart_tools.smartactors.endpoint_components_netty.client_context_binding_handlers;

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

public class StoreBoundRequestHandlerTest extends TrivialPluginsLoadingTestBase {
    private IObject env;
    private IObject req;
    private Channel channel;
    private Attribute channelAttribute;
    private IDefaultMessageContext context;
    private IMessageHandlerCallback callback;

    @Override
    protected void registerMocks() throws Exception {
        env = mock(IObject.class);
        req = mock(IObject.class);
        channel = mock(Channel.class);
        channelAttribute = mock(Attribute.class);
        context = mock(IDefaultMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);

        when(context.getDstMessage()).thenReturn(env);
        when(context.getConnectionContext()).thenReturn(channel);
        when(channel.attr(same(AttributeKeys.REQUEST_ATTRIBUTE_KEY))).thenReturn(channelAttribute);
        when(channelAttribute.get()).thenReturn(req);
    }

    @Test
    public void Should_storeRequestEnvInResponseEnv() throws Exception {
        doAnswer(i -> {
            verify(env).setValue(
                    same(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request")),
                    same(req)
            );

            return null;
        }).when(callback).handle(any());

        new StoreBoundRequestHandler<>().handle(callback, context);

        verify(callback).handle(same(context));
    }
}

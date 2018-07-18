package info.smart_tools.smartactors.endpoint_component_netty.http_path_parse;

import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.parse_tree.IParseTree;
import info.smart_tools.smartactors.endpoint_components_generic.parse_tree.ParseTree;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import io.netty.handler.codec.http.*;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class HttpPathParseTest extends PluginsLoadingTestBase {
    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
        load(IFieldPlugin.class);
    }

    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getOrAdd(IParseTree.class.getCanonicalName()), new SingletonStrategy(new ParseTree()));
    }

    @Test
    public void testUriWithEmptyArgs() throws Exception {
        IObject message = new DSObject();
        IObject messageMessage = mock(IObject.class);
        message.setValue(new FieldName("message"), messageMessage);
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/home/hello/hello");
        IInboundMessageByteArray byteArray = mock(IInboundMessageByteArray.class);
        when(context.getSrcMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getDstMessage()).thenReturn(message);

        IFieldName fieldName = new FieldName("messageMapId");
        HttpPathParse deserializeStrategyGet = new HttpPathParse(new ArrayList<String>(){{add("/home/:messageMapId/hello");}});
        deserializeStrategyGet.handle(next, context);
        verify(messageMessage).setValue(fieldName, "hello");
    }

    @Test
    public void testUriWithArgs() throws Exception {
        IObject message = new DSObject();
        IObject messageMessage = new DSObject();
        message.setValue(new FieldName("message"), messageMessage);
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "/home/hello?hello=world");
        IInboundMessageByteArray byteArray = mock(IInboundMessageByteArray.class);
        when(context.getSrcMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getDstMessage()).thenReturn(message);

        HttpPathParse deserializeStrategyGet = new HttpPathParse(new ArrayList<String>(){{add("/home/:messageMapId");}});
        deserializeStrategyGet.handle(next, context);
        assertEquals(messageMessage.getValue(new FieldName("messageMapId")), "hello");
    }
}

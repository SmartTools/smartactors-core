package info.smart_tools.smartactors.endpoint_components_netty.http_headers_setter;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import io.netty.handler.codec.http.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class HttpHeadersSetterTest extends PluginsLoadingTestBase {
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
    protected void registerMocks() throws Exception {}

    @Test
    public void testSettingHeaders() throws Exception {
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        IOutboundMessageByteArray byteArray = mock(IOutboundMessageByteArray.class);

        HttpHeadersSetter headersSetter = new HttpHeadersSetter();
        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"headers\": [\n" +
                "      {\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\"\n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"hello\",  " +
                "        \"value\": \"world\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}");
        IObject header1 = new DSObject("{\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\"\n" +
                "      }");
        IObject header2 = new DSObject("{\n" +
                "        \"name\": \"hello\", " +
                "        \"value\": \"world\"\n" +
                "      }");

        when(context.getDstMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getSrcMessage()).thenReturn(environment);

        List<String> headersGoodString = new ArrayList<>(2);
        List<IObject> headers = new ArrayList<>(2);
        headers.add(header1);
        headers.add(header2);
        headersGoodString.add("foo=bar");
        headersGoodString.add("hello=world");

        headersSetter.handle(next, context);
        assertEquals("bar", httpMessage.headers().get("foo"));
        assertEquals("world", httpMessage.headers().get("hello"));
    }

    @Test
    public void testSettingEmptyHeaders() throws Exception {
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        IOutboundMessageByteArray byteArray = mock(IOutboundMessageByteArray.class);
        HttpHeadersSetter headersSetter = new HttpHeadersSetter();

        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"headers\": [" +
                "    ]\n" +
                "  }\n" +
                "}");

        when(context.getDstMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getSrcMessage()).thenReturn(environment);

        List<String> headersGoodString = new ArrayList<>(0);
        List<IObject> headers = new ArrayList<>(2);

        headersGoodString.add("foo=bar");
        headersGoodString.add("hello=world");

        headersSetter.handle(next, context);

        assertNull(httpMessage.headers().get("foo"));
        assertNull(httpMessage.headers().get("hello"));
    }
}

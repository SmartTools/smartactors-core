package info.smart_tools.smartactors.endpoint_component_netty.cookies_setter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.field_plugins.ifield_plugin.IFieldPlugin;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import io.netty.handler.codec.http.*;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CookiesSetterTest extends PluginsLoadingTestBase {
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
    public void testCookiesSetting() throws Exception {
        CookiesSetter setter = new CookiesSetter();
        IObject message = new DSObject();
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        IOutboundMessageByteArray byteArray = mock(IOutboundMessageByteArray.class);
        when(context.getDstMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getSrcMessage()).thenReturn(message);

        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"cookies\": [\n" +
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
        IObject messageContext = new DSObject(
                " {" +
                        "    \"cookies\": [\n" +
                        "      {\n" +
                        "        \"name\": \"foo\", " +
                        "        \"value\": \"bar\"\n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"name\": \"hello\",  " +
                        "        \"value\": \"world\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n");
        IObject cookie1 = new DSObject("{\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\"\n" +
                "      }");
        IObject cookie2 = new DSObject("{\n" +
                "        \"name\": \"hello\", " +
                "        \"value\": \"world\"\n" +
                "      }");
        List<String> cookiesGoodString = new ArrayList<>(2);
        cookiesGoodString.add("foo=bar");
        cookiesGoodString.add("hello=world");

        List<IObject> cookies = new ArrayList<>(2);
        cookies.add(cookie1);
        cookies.add(cookie2);
        IObject ctx = new DSObject();
        message.setValue(new FieldName("context"), ctx);
        ctx.setValue(new FieldName("cookies"), cookies);
        setter.handle(next, context);
        List<String> cookiesString = httpMessage.headers().getAll(HttpHeaders.Names.SET_COOKIE);
        for (String cookie : cookiesString) {
            assertEquals(cookiesGoodString.get(0), cookiesString.get(0));
        }
    }

    @Test
    public void testCookiesSettingWithTime_ShouldSetDiscard()
            throws Exception {
        CookiesSetter setter = new CookiesSetter();
        IObject message = new DSObject();
        IMessageHandlerCallback next = mock(IMessageHandlerCallback.class);
        IDefaultMessageContext context = mock(IDefaultMessageContext.class);
        FullHttpMessage httpMessage = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        IOutboundMessageByteArray byteArray = mock(IOutboundMessageByteArray.class);
        when(context.getDstMessage()).thenReturn(byteArray);
        when(byteArray.getMessage()).thenReturn(httpMessage);
        when(context.getSrcMessage()).thenReturn(message);

        IObject environment = new DSObject("{\n" +
                "  \"context\": {\n" +
                "    \"cookies\": [\n" +
                "      {\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\",\n" +
                "         \"maxAge\": 12 \n" +
                "      },\n" +
                "      {\n" +
                "        \"name\": \"hello\",  " +
                "        \"value\": \"world\"\n" +
                "      }\n" +
                "    ]\n" +
                "  }\n" +
                "}");
        IObject messageContext = new DSObject(
                " {" +
                        "    \"cookies\": [\n" +
                        "      {\n" +
                        "        \"name\": \"foo\", " +
                        "        \"value\": \"bar\",\n" +
                        "         \"maxAge\": 12 \n" +
                        "      },\n" +
                        "      {\n" +
                        "        \"name\": \"hello\",  " +
                        "        \"value\": \"world\"\n" +
                        "      }\n" +
                        "    ]\n" +
                        "  }\n");
        IObject cookie1 = new DSObject("{\n" +
                "        \"name\": \"foo\", " +
                "        \"value\": \"bar\",\n" +
                "         \"maxAge\": 12 \n" +
                "      }");
        IObject cookie2 = new DSObject("{\n" +
                "        \"name\": \"hello\", " +
                "        \"value\": \"world\"\n" +
                "      }");
        List<IObject> cookies = new ArrayList<>(2);
        cookies.add(cookie1);
        cookies.add(cookie2);

        IObject ctx = new DSObject();
        message.setValue(new FieldName("context"), ctx);
        ctx.setValue(new FieldName("cookies"), cookies);
        setter.handle(next, context);

        List<String> cookiesString = httpMessage.headers().getAll(HttpHeaders.Names.SET_COOKIE);
        assertTrue(cookiesString.get(0).lastIndexOf("Expires")>0);
        assertFalse(cookiesString.get(1).lastIndexOf("Expires")>0);
    }
}

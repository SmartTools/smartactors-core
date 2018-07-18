package info.smart_tools.smartactors.endpoint_components_netty.http_query_string_parser;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IInboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.HttpRequest;
import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HttpQueryStringParserTest extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext messageContext;
    private IMessageHandlerCallback callback;

    @Override
    protected void registerMocks() throws Exception {
        messageContext = new DefaultMessageContextImplementation();
        callback = mock(IMessageHandlerCallback.class);

        messageContext.setDstMessage(IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'message':{}}".replace('\'','"')));

    }

    private void setMessageWithURI(String uri) throws Exception {
        IInboundMessageByteArray<HttpRequest> inboundMessageByteArray = mock(IInboundMessageByteArray.class);
        HttpRequest request = mock(HttpRequest.class);

        when(inboundMessageByteArray.getMessage()).thenReturn(request);
        when(request.uri()).thenReturn(uri);

        messageContext.setSrcMessage(inboundMessageByteArray);
    }

    @Test public void Should_parseQueryString() throws Exception {
        setMessageWithURI("?foo=bar;bar=baz&baz=foo;lst=1;lst=2");

        doAnswer(invocationOnMock -> {
            IObject env = (IObject) messageContext.getDstMessage();
            IObject msg = (IObject) env.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"));

            assertEquals("bar", msg
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "foo")));
            assertEquals("baz", msg
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "bar")));
            assertEquals("foo", msg
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "baz")));
            assertEquals(Arrays.asList("1", "2"), msg
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "lst")));

            return null;
        }).when(callback).handle(any());

        new HttpQueryStringParser<>().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }
}

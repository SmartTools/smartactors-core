package info.smart_tools.smartactors.endpoint_components_netty.http_status_setter;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_byte_array.IOutboundMessageByteArray;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class HttpStatusSetterTest extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext<IObject, IOutboundMessageByteArray<FullHttpResponse>, Object> messageContext;
    private FullHttpResponse httpResponse;
    private IObject env;
    private IMessageHandlerCallback<IDefaultMessageContext<IObject, IOutboundMessageByteArray<FullHttpResponse>, Object>> callback;

    @Override protected void registerMocks() throws Exception {
        messageContext = mock(IDefaultMessageContext.class);
        callback = mock(IMessageHandlerCallback.class);

        when(messageContext.getDstMessage()).thenReturn(mock(IOutboundMessageByteArray.class));
        when(messageContext.getDstMessage().getMessage()).thenReturn(httpResponse = mock(FullHttpResponse.class));

        when(messageContext.getSrcMessage()).thenAnswer(inv -> env);
    }

    @Test public void Should_setCodeIfCodeGiven() throws Exception {
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'context':{'httpResponseStatusCode':418}}".replace('\'','"'));

        doAnswer(inv -> {
            ArgumentCaptor<HttpResponseStatus> statusArgumentCaptor = ArgumentCaptor.forClass(HttpResponseStatus.class);
            verify(httpResponse).setStatus(statusArgumentCaptor.capture());
            assertNotNull(statusArgumentCaptor.getValue());
            assertEquals(418, statusArgumentCaptor.getValue().code());
            return null;
        }).when(callback).handle(any());

        new HttpStatusSetter<>().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }

    @Test public void Should_notSetCodeIfNoCodeGiven() throws Exception {
        env = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()),
                "{'context':{}}".replace('\'','"'));

        new HttpStatusSetter<>().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
        verifyNoMoreInteractions(httpResponse);
    }
}

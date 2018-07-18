package info.smart_tools.smartactors.endpoint_components_generic.client_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchcked")
public class ErrorClientHandlerTest extends TrivialPluginsLoadingTestBase {
    private IClientCallback callbackMock;
    private IObject request;
    private IObject response;
    private IMessageHandlerCallback nextCallback;
    private IDefaultMessageContext context;
    private Throwable err = new Throwable();

    @Override
    protected void registerMocks() throws Exception {
        callbackMock = mock(IClientCallback.class);

        request = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        request.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "callback"),
                callbackMock);
        response = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        response.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "request"),
                request);

        nextCallback = mock(IMessageHandlerCallback.class);
        context = mock(IDefaultMessageContext.class);

        when(context.getDstMessage()).thenReturn(response);
        when(context.getSrcMessage()).thenReturn(err);

        doThrow(new AssertionError()).when(nextCallback).handle(any());
    }

    @Test
    public void Should_callErrorMethod() throws Exception {
        new ErrorClientHandler().handle(nextCallback, context);

        verify(callbackMock).onError(same(request), same(err));
    }
}

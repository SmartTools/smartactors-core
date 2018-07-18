package info.smart_tools.smartactors.endpoint_components_generic.client_handlers;

import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.IClientCallback;
import info.smart_tools.smartactors.endpoint_interfaces.iclient_callback.exceptions.ClientCallbackException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

@SuppressWarnings("unchecked")
public class StartClientHandlerTest extends TrivialPluginsLoadingTestBase {
    private IClientCallback callbackMock;
    private IObject request;
    private IMessageHandlerCallback nextCallback;
    private IDefaultMessageContext context;

    @Override
    protected void registerMocks() throws Exception {
        callbackMock = mock(IClientCallback.class);

        request = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        request.setValue(
                IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "callback"),
                callbackMock);

        nextCallback = mock(IMessageHandlerCallback.class);
        context = mock(IDefaultMessageContext.class);

        when(context.getSrcMessage()).thenReturn(request);
    }

    @Test
    public void Should_callCallbackAndNext() throws Exception {
        doAnswer(i -> {
            verify(callbackMock).onStart(same(request));
            return null;
        }).when(nextCallback).handle(any());

        new StartClientHandler().handle(nextCallback, context);

        verify(nextCallback).handle(same(context));
    }

    @Test
    public void Should_notifyCallbackWhenNextThrows() throws Exception {
        Throwable err = new MessageHandlerException();
        doThrow(err).when(nextCallback).handle(any());

        try {
            new StartClientHandler().handle(nextCallback, context);
            fail();
        } catch (MessageHandlerException ignore) { }

        verify(callbackMock).onError(same(request), same(err));
    }

    @Test
    public void Should_notCallErrorMethodWhenExceptionIsThrownByStartMethod() throws Exception {
        Throwable err = new ClientCallbackException();
        doThrow(err).when(callbackMock).onStart(any());

        try {
            new StartClientHandler().handle(nextCallback, context);
            fail();
        } catch (MessageHandlerException e) {
            assertSame(err, e.getCause());
        }

        verify(callbackMock).onStart(same(request));
        verifyNoMoreInteractions(callbackMock);
    }
}

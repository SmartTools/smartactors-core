package info.smart_tools.smartactors.endpoint_components_generic.create_environment_message_handler;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateEnvironmentMessageHandlerTest extends TrivialPluginsLoadingTestBase {
    private IDefaultMessageContext messageContext;
    private IMessageHandlerCallback callback;

    @Override
    protected void registerMocks() throws Exception {
        messageContext = new DefaultMessageContextImplementation();
        callback = mock(IMessageHandlerCallback.class);
    }

    @Test public void Should_createEnvironmentAndContext() throws Exception {
        doAnswer(invocationOnMock -> {
            assertSame(messageContext, invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class));
            assertNotNull(messageContext.getDstMessage());
            assertTrue(messageContext.getDstMessage() instanceof IObject);
            IObject context = (IObject) (((IObject) messageContext.getDstMessage())
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context")));

            assertNotNull(context);

            assertEquals(Boolean.TRUE, context
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "fromExternal")));
            return null;
        }).when(callback).handle(any());
        new CreateEnvironmentMessageHandler().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }
}

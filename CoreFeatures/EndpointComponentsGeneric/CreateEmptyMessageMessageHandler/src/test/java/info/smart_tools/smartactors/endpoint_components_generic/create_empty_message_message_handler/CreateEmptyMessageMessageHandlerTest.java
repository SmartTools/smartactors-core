package info.smart_tools.smartactors.endpoint_components_generic.create_empty_message_message_handler;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
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
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class CreateEmptyMessageMessageHandlerTest extends TrivialPluginsLoadingTestBase {
    private IMessageHandlerCallback callback;
    private IDefaultMessageContext messageContext;

    @Override protected void registerMocks() throws Exception {
        callback = mock(IMessageHandlerCallback.class);
        messageContext = new DefaultMessageContextImplementation();
        messageContext.setDstMessage(IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
    }

    @Test public void Should_putEmptyMessageToDestinationEnvironment() throws Exception {
        doAnswer(invocationOnMock -> {
            assertSame(messageContext, invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class));
            IObject env = (IObject) messageContext.getDstMessage();
            IObject msg = (IObject) env.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message"));
            assertNotNull(msg);
            assertEquals("{}", msg.serialize());
            return null;
        }).when(callback).handle(any());

        new CreateEmptyMessageMessageHandler().handle(callback, messageContext);

        verify(callback).handle(same(messageContext));
    }
}

package info.smart_tools.smartactors.endpoint_components_generic.response_strategy_set_message_handler;

import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_components_generic.endpoint_response_strategy.EndpointResponseStrategy;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class ResponseStrategySetMessageHandlerTest extends TrivialPluginsLoadingTestBase {
    private Object connectionContext = new Object();

    private IDefaultMessageContext messageContext;
    private IMessageHandlerCallback callbackMock;
    private EndpointResponseStrategy responseStrategyMock;

    private IObject dstEnv, dstCtx;

    @Override
    protected void registerMocks() throws Exception {
        messageContext = new DefaultMessageContextImplementation();
        callbackMock = mock(IMessageHandlerCallback.class);
        responseStrategyMock = mock(EndpointResponseStrategy.class);

        dstEnv = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        dstCtx = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        dstEnv.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), dstCtx);
        messageContext.setDstMessage(dstEnv);
        messageContext.setConnectionContext(connectionContext);
    }

    @Test
    public void Should_storeResponseStrategyAndConnectionContextInDestinationMessageContext()
            throws Exception {
        ResponseStrategySetMessageHandler messageHandler = new ResponseStrategySetMessageHandler(responseStrategyMock);

        messageHandler.handle(callbackMock, messageContext);

        verify(callbackMock).handle(same(messageContext));

        assertSame(responseStrategyMock,
                dstCtx.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseStrategy")));
        assertSame(connectionContext,
                dstCtx.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext")));
    }
}

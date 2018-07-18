package info.smart_tools.smartactors.endpoint_components_generic.endpoint_response_strategy;


import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.endpoint_components_generic.default_message_context_implementation.DefaultMessageContextImplementation;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.helpers.trivial_plugins_loading_test_base.TrivialPluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import static org.junit.Assert.*;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class EndpointResponseStrategyTest extends TrivialPluginsLoadingTestBase {
    private Object connectionContext = new Object();

    private IObject reqEnv, reqCtx, response;

    private IMessageHandlerCallback responsePipelineMock;
    private IFunction0 responseCtxProvider = DefaultMessageContextImplementation::new;

    @Override
    protected void registerMocks() throws Exception {
        reqEnv = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        reqCtx = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        response = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));

        reqEnv.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context"), reqCtx);
        reqEnv.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "response"), response);
        reqCtx.setValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "connectionContext"), connectionContext);

        responsePipelineMock = mock(IMessageHandlerCallback.class);
    }

    @Test
    public void Should_formResponseEnvironmentAndSendItToResponsePipeline()
            throws Exception {
        doAnswer(invocationOnMock -> {
            IDefaultMessageContext context = invocationOnMock.getArgumentAt(0, IDefaultMessageContext.class);
            assertNotNull(context.getSrcMessage());
            assertTrue(context.getSrcMessage() instanceof IObject);
            assertNotSame(reqEnv, context.getSrcMessage());
            assertSame(reqCtx, ((IObject) context.getSrcMessage())
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context")));
            assertSame(response, ((IObject) context.getSrcMessage())
                    .getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message")));
            return null;
        }).when(responsePipelineMock).handle(any());

        EndpointResponseStrategy endpointResponseStrategy = new EndpointResponseStrategy(responsePipelineMock, responseCtxProvider);

        endpointResponseStrategy.sendResponse(reqEnv);

        verify(responsePipelineMock, times(1)).handle(any());

        assertEquals(true, reqCtx.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseSent")));
    }
}

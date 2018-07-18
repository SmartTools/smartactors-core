package info.smart_tools.smartactors.endpoint_components_generic.default_outbound_connection_channel;

import info.smart_tools.smartactors.base.interfaces.iaction.IFunction0;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline.IEndpointPipeline;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DefaultOutboundConnectionChannelTest {
    private IEndpointPipeline pipelineMock;
    private IFunction0 contextFactoryMock;
    private IMessageHandlerCallback callbackMock;
    private IDefaultMessageContext messageContextMock;
    private IObject envMock;
    private Object connectionContext = new Object();

    @Before public void setUp() throws Exception {
        pipelineMock = mock(IEndpointPipeline.class);
        contextFactoryMock = mock(IFunction0.class);
        callbackMock = mock(IMessageHandlerCallback.class);
        messageContextMock = mock(IDefaultMessageContext.class);
        envMock = mock(IObject.class);

        when(pipelineMock.getContextFactory()).thenReturn(contextFactoryMock);
        when(pipelineMock.getInputCallback()).thenReturn(callbackMock);

        when(contextFactoryMock.execute())
                .thenReturn(messageContextMock).thenThrow(FunctionExecutionException.class);
    }

    @Test public void Should_sendMessageToPipeline() throws Exception {
        doAnswer(invocationOnMock -> {
            verify(messageContextMock).setConnectionContext(same(connectionContext));
            verify(messageContextMock).setSrcMessage(same(envMock));
            return null;
        }).when(callbackMock).handle(any());

        new DefaultOutboundConnectionChannel(pipelineMock, connectionContext).send(envMock);

        verify(callbackMock).handle(same(messageContextMock));
    }
}

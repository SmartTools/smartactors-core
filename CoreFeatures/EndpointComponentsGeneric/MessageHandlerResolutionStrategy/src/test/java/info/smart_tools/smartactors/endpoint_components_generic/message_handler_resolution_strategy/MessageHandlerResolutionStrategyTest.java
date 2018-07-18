package info.smart_tools.smartactors.endpoint_components_generic.message_handler_resolution_strategy;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.endpoint_interfaces.iendpoint_pipeline_set.IEndpointPipelineSet;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link MessageHandlerResolutionStrategy}.
 */
public class MessageHandlerResolutionStrategyTest {
    @Test public void Should_callFunction() throws Exception {
        MessageHandlerResolutionStrategy.Function functionMock = mock(MessageHandlerResolutionStrategy.Function.class);

        String type = "typename";
        IObject handlerConf = mock(IObject.class), epConf = mock(IObject.class);
        IEndpointPipelineSet pipelineSet = mock(IEndpointPipelineSet.class);

        IMessageHandler handler = mock(IMessageHandler.class);

        when(functionMock.resolve(same(type), same(handlerConf), same(epConf), same(pipelineSet))).thenReturn(handler);

        IMessageHandler rHandler = new MessageHandlerResolutionStrategy(functionMock)
                .resolve(type, handlerConf, epConf, pipelineSet);

        assertSame(handler, rHandler);
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenNotEnoughArgumentsGiven() throws Exception {
        MessageHandlerResolutionStrategy.Function functionMock = mock(MessageHandlerResolutionStrategy.Function.class);

        String type = "typename";
        IObject handlerConf = mock(IObject.class), epConf = mock(IObject.class);

        new MessageHandlerResolutionStrategy(functionMock)
                .resolve(type, handlerConf, epConf);
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenArgumentTypesDoNotMatch() throws Exception {
        MessageHandlerResolutionStrategy.Function functionMock = mock(MessageHandlerResolutionStrategy.Function.class);

        new MessageHandlerResolutionStrategy(functionMock)
                .resolve(new Object(), new Object(), new Object(), new Object());
    }
}

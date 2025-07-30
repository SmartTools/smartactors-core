package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test for {@link RouterRegistrationObjectListener}.
 */
public class RouterRegistrationObjectListenerTest extends IOCInitializer {
    private IRouter routerMock;
    private IMessageReceiver[] receiverMocks;
    private IStrategy routerStrategy;

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        routerMock = mock(IRouter.class);
        routerStrategy = mock(IStrategy.class);

        when(routerStrategy.resolve()).thenReturn(routerMock);

        IOC.register(Keys.getKeyByName(IRouter.class.getCanonicalName()), routerStrategy);

        receiverMocks = new IMessageReceiver[] {
            mock(IMessageReceiver.class),
            mock(IMessageReceiver.class),
        };
    }

    @Test
    public void Should_registerReceiversInRouter()
            throws Exception {
        IReceiverObjectListener listener = new RouterRegistrationObjectListener();

        listener.acceptItem("id1", receiverMocks[0]);
        listener.acceptItem("id2", receiverMocks[1]);
        listener.endItems();

        verify(routerMock).register("id1", receiverMocks[0]);
        verify(routerMock).register("id2", receiverMocks[1]);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenItemIsNull()
            throws Exception {
        new RouterRegistrationObjectListener().acceptItem("idx", null);
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwWhenItemIdentifierIsUndefined()
            throws Exception {
        new RouterRegistrationObjectListener().acceptItem(null, receiverMocks[0]);
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwWhenItemIsNotAReceiver()
            throws Exception {
        new RouterRegistrationObjectListener().acceptItem("idx", new Object());
    }

    @Test(expected = ReceiverObjectListenerException.class)
    public void Should_throwWhenRouterResolutionFails()
            throws Exception {
        when(routerStrategy.resolve()).thenThrow(StrategyException.class);
        new RouterRegistrationObjectListener().acceptItem("idx", receiverMocks[0]);
    }
}

package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link GenericDecoratorReceiverObjectCreator}.
 */
public class GenericDecoratorReceiverObjectCreatorTest extends IOCInitializer {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator creatorMock;
    private IMessageReceiver[] receiverMocks;
    private IStrategy decoratorReceiverResolutionStrategy;
    private IObject filterConfig, objectConfig, context;

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        listenerMock = mock(IReceiverObjectListener.class);
        creatorMock = mock(IReceiverObjectCreator.class);
        receiverMocks = new IMessageReceiver[] {
            mock(IMessageReceiver.class),
            mock(IMessageReceiver.class),
        };
        decoratorReceiverResolutionStrategy = mock(IStrategy.class);
        filterConfig = mock(IObject.class);
        objectConfig = mock(IObject.class);
        context = mock(IObject.class);

        IOC.register(Keys.getKeyByName("create some receiver decorator"), decoratorReceiverResolutionStrategy);

        when(decoratorReceiverResolutionStrategy.resolve(
                same(receiverMocks[0]), same(filterConfig), same(objectConfig), same(context))).thenReturn(receiverMocks[1]);

        when(filterConfig.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "decoratorDependency")))
                .thenReturn("create some receiver decorator");

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem("foo", receiverMocks[0]);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(creatorMock).create(any(), same(objectConfig), same(context));
    }

    @Test
    public void Should_createAndReturnDecorator()
            throws Exception {
        IReceiverObjectCreator tested = new GenericDecoratorReceiverObjectCreator(creatorMock, filterConfig, objectConfig);

        tested.create(listenerMock, objectConfig, context);

        verify(listenerMock).acceptItem(eq("foo"), same(receiverMocks[1]));
        verify(listenerMock).endItems();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test(expected = ReceiverObjectListenerException.class)
    public void Should_wrapExceptions()
            throws Exception {
        when(decoratorReceiverResolutionStrategy.resolve(any(), any(), any(), any())).thenThrow(StrategyException.class);

        IReceiverObjectCreator tested = new GenericDecoratorReceiverObjectCreator(creatorMock, filterConfig, objectConfig);

        tested.create(listenerMock, objectConfig, context);
    }
}

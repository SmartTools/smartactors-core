package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Collections;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link HandlerRouterReceiverCreator}.
 */
public class HandlerRouterReceiverCreatorTest extends IOCInitializer {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator creatorMock;
    private IMessageReceiver[] receiverMocks;
    private IStrategy handlerRouterReceiverResolutionStrategy;
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
                mock(IMessageReceiver.class),
                mock(IMessageReceiver.class),
        };

        handlerRouterReceiverResolutionStrategy = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("create handler router receiver"), handlerRouterReceiverResolutionStrategy);

        when(handlerRouterReceiverResolutionStrategy.resolve(any())).thenReturn(receiverMocks[0]);

        filterConfig = mock(IObject.class);
        objectConfig = mock(IObject.class);
        context = mock(IObject.class);
    }

    @Test
    public void Should_returnOneUndefinedIdentifier()
            throws Exception {
        assertEquals(
                Collections.singletonList(null),
                new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig).enumIdentifiers(objectConfig, context)
        );
    }

    @Test
    public void Should_createHandlerRoutingReceiver()
            throws Exception {
        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("id1", receiverMocks[1]);
        listenerCaptor.getValue().acceptItem("id2", receiverMocks[2]);
        listenerCaptor.getValue().acceptItem("id3", receiverMocks[3]);

        verify(listenerMock, times(0)).acceptItem(any(), any());
        verify(listenerMock, times(0)).endItems();

        listenerCaptor.getValue().endItems();

        ArgumentCaptor<Map> mapCaptor = ArgumentCaptor.forClass(Map.class);
        verify(handlerRouterReceiverResolutionStrategy).resolve(mapCaptor.capture());
        verify(listenerMock, times(1)).acceptItem(isNull(), same(receiverMocks[0]));
        verify(listenerMock, times(1)).endItems();

        assertSame(receiverMocks[1], mapCaptor.getValue().get("id1"));
        assertSame(receiverMocks[2], mapCaptor.getValue().get("id2"));
        assertSame(receiverMocks[3], mapCaptor.getValue().get("id3"));
    }

    @Test(expected = ReceiverObjectListenerException.class)
    public void Should_throwWhenRouterCreationStrategyThrows()
            throws Exception {
        when(handlerRouterReceiverResolutionStrategy.resolve(any())).thenThrow(StrategyException.class);

        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("id1", receiverMocks[1]);
        listenerCaptor.getValue().acceptItem("id2", receiverMocks[2]);
        listenerCaptor.getValue().acceptItem("id3", receiverMocks[3]);

        verify(listenerMock, times(0)).acceptItem(any(), any());
        verify(listenerMock, times(0)).endItems();

        listenerCaptor.getValue().endItems();
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwIfReceiversWithDuplicateIdentifiersPassed()
            throws Exception {
        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idz", receiverMocks[1]);
        listenerCaptor.getValue().acceptItem("idz", receiverMocks[2]);
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwIfAnyOfItemsHasUndefinedIdentifier()
            throws Exception {
        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem(null, receiverMocks[1]);
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwIfItemIsNotAReceiver()
            throws Exception {
        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idz", new Object());
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwIfItemIsNull()
            throws Exception {
        IReceiverObjectCreator creator = new HandlerRouterReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idz", null);
    }
}

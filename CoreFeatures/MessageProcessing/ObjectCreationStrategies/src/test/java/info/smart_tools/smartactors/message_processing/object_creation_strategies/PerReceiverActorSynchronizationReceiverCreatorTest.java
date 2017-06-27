package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.InvalidReceiverPipelineException;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link PerReceiverActorSynchronizationReceiverCreator}.
 */
public class PerReceiverActorSynchronizationReceiverCreatorTest extends PluginsLoadingTestBase {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator creatorMock;
    private IMessageReceiver[] receiverMocks;
    private IResolveDependencyStrategy actorReceiverResolutionStrategy;
    private IObject filterConfig, objectConfig, context;

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
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

        actorReceiverResolutionStrategy = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("create actor synchronization receiver"), actorReceiverResolutionStrategy);

        when(actorReceiverResolutionStrategy.resolve(same(receiverMocks[0]))).thenReturn(receiverMocks[1]);
        when(actorReceiverResolutionStrategy.resolve(same(receiverMocks[2]))).thenReturn(receiverMocks[3]);

        filterConfig = mock(IObject.class);
        objectConfig = mock(IObject.class);
        context = mock(IObject.class);
    }

    @Test
    public void Should_createActorDecoratorsForAllReceivers()
            throws Exception {
        IReceiverObjectCreator creator = new PerReceiverActorSynchronizationReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("id1", receiverMocks[0]);
        listenerCaptor.getValue().acceptItem("id2", receiverMocks[2]);

        verify(listenerMock).acceptItem(eq("id1"), same(receiverMocks[1]));
        verify(listenerMock).acceptItem(eq("id2"), same(receiverMocks[3]));

        reset(listenerMock);

        listenerCaptor.getValue().endItems();
        verify(listenerMock).endItems();
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_throwWhenItemIsNotAReceiver()
            throws Exception {
        IReceiverObjectCreator creator = new PerReceiverActorSynchronizationReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idx", new Object());
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenItemIsNull()
            throws Exception {
        IReceiverObjectCreator creator = new PerReceiverActorSynchronizationReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idx", null);
    }

    @Test(expected = ReceiverObjectListenerException.class)
    public void Should_throwWhenActorReceiverCreationStrategyThrows()
            throws Exception {
        when(actorReceiverResolutionStrategy.resolve(same(receiverMocks[0]))).thenThrow(ResolveDependencyStrategyException.class);

        IReceiverObjectCreator creator = new PerReceiverActorSynchronizationReceiverCreator(creatorMock, filterConfig, objectConfig);

        creator.create(listenerMock, objectConfig, context);

        ArgumentCaptor<IReceiverObjectListener> listenerCaptor = ArgumentCaptor.forClass(IReceiverObjectListener.class);
        verify(creatorMock).create(listenerCaptor.capture(), same(objectConfig), same(context));

        listenerCaptor.getValue().acceptItem("idx", receiverMocks[0]);
    }
}

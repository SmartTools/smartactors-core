package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectListenerException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link GenericDecoratorReceiverObjectCreator}.
 */
public class GenericDecoratorReceiverObjectCreatorTest extends PluginsLoadingTestBase {
    private IReceiverObjectListener listenerMock;
    private IReceiverObjectCreator creatorMock;
    private IMessageReceiver[] receiverMocks;
    private IResolveDependencyStrategy decoratorReceiverResolutionStrategy;
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
        };
        decoratorReceiverResolutionStrategy = mock(IResolveDependencyStrategy.class);
        filterConfig = mock(IObject.class);
        objectConfig = mock(IObject.class);
        context = mock(IObject.class);

        IOC.register(Keys.getOrAdd("create some receiver decorator"), decoratorReceiverResolutionStrategy);

        when(decoratorReceiverResolutionStrategy.resolve(same(receiverMocks[0]), same(filterConfig))).thenReturn(receiverMocks[1]);

        when(filterConfig.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "decoratorDependency")))
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
        when(decoratorReceiverResolutionStrategy.resolve(any(), any())).thenThrow(ResolveDependencyStrategyException.class);

        IReceiverObjectCreator tested = new GenericDecoratorReceiverObjectCreator(creatorMock, filterConfig, objectConfig);

        tested.create(listenerMock, objectConfig, context);
    }
}

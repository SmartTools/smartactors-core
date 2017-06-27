package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
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
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link UserObjectMethodInvokerReceiverCreator}.
 */
public class UserObjectMethodInvokerReceiverCreatorTest extends PluginsLoadingTestBase {
    public static class AObject {
        public void method1() {
        }

        public void method2() {
        }
    }

    private IObject configMock;
    private IObject filterConfigMock;
    private IObject contextMock;
    private IReceiverObjectCreator underlyingCreatorMock;
    private IReceiverObjectListener listenerMock;
    private AObject object = new AObject();
    private IMessageReceiver[] invokerMocks;
    private IResolveDependencyStrategy invokerResolutionStrategy;

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
        configMock = mock(IObject.class);
        filterConfigMock = mock(IObject.class);
        contextMock = mock(IObject.class);
        underlyingCreatorMock = mock(IReceiverObjectCreator.class);
        listenerMock = mock(IReceiverObjectListener.class);
        invokerMocks = new IMessageReceiver[] {
            mock(IMessageReceiver.class),
            mock(IMessageReceiver.class),
        };
        invokerResolutionStrategy = mock(IResolveDependencyStrategy.class);

        IOC.register(Keys.getOrAdd("method invoker receiver"), invokerResolutionStrategy);

        when(invokerResolutionStrategy.resolve(
                same(object),
                eq(AObject.class.getMethod("method1")),
                same(filterConfigMock)
        )).thenReturn(invokerMocks[0]);
        when(invokerResolutionStrategy.resolve(
                same(object),
                eq(AObject.class.getMethod("method2")),
                same(filterConfigMock)
        )).thenReturn(invokerMocks[1]);

        doAnswer(invocation -> {
            invocation.getArgumentAt(0, IReceiverObjectListener.class).acceptItem(null, object);
            invocation.getArgumentAt(0, IReceiverObjectListener.class).endItems();
            return null;
        }).when(underlyingCreatorMock).create(any(), any(), any());
    }

    @Test
    public void Should_resolveInvokersForObjectMethods()
            throws Exception {
        IReceiverObjectCreator creator = new UserObjectMethodInvokerReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        creator.create(listenerMock, configMock, contextMock);

        verify(listenerMock).acceptItem(eq("method1"), same(invokerMocks[0]));
        verify(listenerMock).acceptItem(eq("method2"), same(invokerMocks[1]));
        verify(listenerMock).endItems();
        verifyNoMoreInteractions(listenerMock);
    }

    @Test(expected = InvalidReceiverPipelineException.class)
    public void Should_notSupportItemEnumeration()
            throws Exception {
        IReceiverObjectCreator creator = new UserObjectMethodInvokerReceiverCreator(underlyingCreatorMock, filterConfigMock, configMock);

        creator.enumIdentifiers(configMock, contextMock);
    }
}

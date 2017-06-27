package info.smart_tools.smartactors.message_processing.object_creation_strategies;

import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.exception.ResolveDependencyStrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link MethodInvokerReceiverResolutionStrategy}.
 */
public class MethodInvokerReceiverResolutionStrategyTest extends PluginsLoadingTestBase {
    public class AObject {
        public void method1(Object o) {
        }

        public void method2(Object o, Object o2) {
        }
    }

    private AObject object = new AObject();

    private IResolveDependencyStrategy strategy;

    private IObject configMock;
    private IReceiverGenerator receiverGeneratorMock;
    private IResolveDependencyStrategy defaultWrapperResolutionStrategyMock;
    private IResolveDependencyStrategy specialWrapperResolutionStrategyMock;
    private IResolveDependencyStrategy defaultWrapperResolutionStrategyResolutionStrategyMock;
    private IResolveDependencyStrategy specialWrapperResolutionStrategyResolutionStrategyMock;
    private IMessageReceiver receiverMock1;
    private IMessageReceiver receiverMock2;

    @Override
    protected void registerMocks() throws Exception {
        receiverGeneratorMock = mock(IReceiverGenerator.class);

        IOC.register(Keys.getOrAdd(IReceiverGenerator.class.getCanonicalName()), new SingletonStrategy(receiverGeneratorMock));

        configMock = mock(IObject.class);

        strategy = new MethodInvokerReceiverResolutionStrategy();

        defaultWrapperResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        specialWrapperResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        defaultWrapperResolutionStrategyResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        specialWrapperResolutionStrategyResolutionStrategyMock = mock(IResolveDependencyStrategy.class);
        receiverMock1 = mock(IMessageReceiver.class);
        receiverMock2 = mock(IMessageReceiver.class);

        when(defaultWrapperResolutionStrategyResolutionStrategyMock.resolve(same(Object.class)))
                .thenReturn(defaultWrapperResolutionStrategyMock);
        when(specialWrapperResolutionStrategyResolutionStrategyMock.resolve(same(Object.class)))
                .thenReturn(specialWrapperResolutionStrategyMock);

        when(receiverGeneratorMock.generate(same(object), same(defaultWrapperResolutionStrategyMock), eq("method1")))
                .thenReturn(receiverMock1);
        when(receiverGeneratorMock.generate(same(object), same(specialWrapperResolutionStrategyMock), eq("method1")))
                .thenReturn(receiverMock2);

        IOC.register(Keys.getOrAdd("default wrapper resolution strategy dependency for invoker receiver"),
                defaultWrapperResolutionStrategyResolutionStrategyMock);
        IOC.register(Keys.getOrAdd("special wrapper resolution strategy dependency for invoker receiver"),
                specialWrapperResolutionStrategyResolutionStrategyMock);
    }

    @Override
    protected void loadPlugins() throws Exception {
        load(ScopedIOCPlugin.class);
        load(PluginScopeProvider.class);
        load(PluginIOCKeys.class);
        load(PluginDSObject.class);
        load(IFieldNamePlugin.class);
    }

    @Test(expected = ResolveDependencyStrategyException.class)
    public void Should_throwWhenMethodHasTooMuchArguments()
            throws Exception {
        strategy.resolve(
                object,
                object.getClass().getMethod("method2", Object.class, Object.class),
                configMock
        );
    }

    @Test
    public void Should_generateReceiverWithDefaultWrapperStrategy()
            throws Exception {
        assertSame(receiverMock1, strategy.resolve(
                object,
                object.getClass().getMethod("method1", Object.class),
                configMock
        ));
    }

    @Test
    public void Should_generateReceiverWithSpecificWrapperStrategy()
            throws Exception {
        when(configMock.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "wrapperResolutionStrategyDependency")))
                .thenReturn("special wrapper resolution strategy dependency for invoker receiver");

        assertSame(receiverMock2, strategy.resolve(
                object,
                object.getClass().getMethod("method1", Object.class),
                configMock
        ));
    }
}

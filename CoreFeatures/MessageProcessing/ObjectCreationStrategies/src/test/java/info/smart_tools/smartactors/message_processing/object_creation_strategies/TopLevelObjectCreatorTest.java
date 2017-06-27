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
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectCreator;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.IReceiverObjectListener;
import info.smart_tools.smartactors.message_processing_interfaces.object_creation_interfaces.exeptions.ReceiverObjectCreatorException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link TopLevelObjectCreator}.
 */
public class TopLevelObjectCreatorTest extends PluginsLoadingTestBase {
    private IObject configMock;
    private IObject contextMock;
    private IReceiverObjectListener listenerMock;
    private IResolveDependencyStrategy objectResolutionStrategy;
    private Object object = new Object();

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
        contextMock = mock(IObject.class);
        listenerMock = mock(IReceiverObjectListener.class);
        objectResolutionStrategy = mock(IResolveDependencyStrategy.class);

        IOC.register(Keys.getOrAdd("the object dependency"), objectResolutionStrategy);

        when(objectResolutionStrategy.resolve(same(configMock))).thenReturn(object);

        when(configMock.getValue(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "dependency")))
                .thenReturn("the object dependency");
    }

    @Test
    public void Should_resolveObjectNotifyListenerAndStoreItInContext()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        creator.create(listenerMock, configMock, contextMock);

        verify(objectResolutionStrategy, times(1)).resolve(any());
        verify(listenerMock).acceptItem(isNull(), same(object));
        verify(contextMock).setValue(eq(IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "topLevelObject")), same(object));
    }

    @Test
    public void Should_returnListOfOneIdentifier()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        assertEquals(Collections.singletonList(null), creator.enumIdentifiers(configMock, contextMock));
    }

    @Test(expected = ReceiverObjectCreatorException.class)
    public void Should_wrapExceptionWhenErrorOccursResolvingObject()
            throws Exception {
        IReceiverObjectCreator creator = new TopLevelObjectCreator();

        when(objectResolutionStrategy.resolve(any())).thenThrow(ResolveDependencyStrategyException.class);

        creator.create(listenerMock, configMock, contextMock);
    }
}

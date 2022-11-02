package info.smart_tools.smartactors.statistics.sensors.embedded_sensor;

import info.smart_tools.smartactors.helpers.plugins_loading_test_base.PluginsLoadingTestBase;
import info.smart_tools.smartactors.iobject_plugins.dsobject_plugin.PluginDSObject;
import info.smart_tools.smartactors.iobject_plugins.ifieldname_plugin.IFieldNamePlugin;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainModificationException;
import info.smart_tools.smartactors.scope_plugins.scope_provider_plugin.PluginScopeProvider;
import info.smart_tools.smartactors.scope_plugins.scoped_ioc_plugin.ScopedIOCPlugin;
import info.smart_tools.smartactors.statistics.sensors.interfaces.exceptions.SensorShutdownException;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Test for {@link EmbeddedSensorHandle}.
 */
public class EmbeddedSensorHandleTest extends PluginsLoadingTestBase {
    private IChainStorage chainStorageMock;
    private final Object modId = new Object();
    private final Object chainId = new Object();

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
        chainStorageMock = mock(IChainStorage.class);
    }

    @Test
    public void Should_rollbackTargetChainModification()
            throws Exception {
        new EmbeddedSensorHandle(chainStorageMock, chainId, modId).shutdown();

        verify(chainStorageMock).rollback(chainId, modId);
    }

    @Test(expected = SensorShutdownException.class)
    public void Should_wrapExceptionThrownByRollbackMethod()
            throws Exception {
        doThrow(ChainModificationException.class).when(chainStorageMock).rollback(chainId, modId);
        new EmbeddedSensorHandle(chainStorageMock, chainId, modId).shutdown();
    }
}

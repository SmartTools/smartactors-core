package info.smart_tools.smartactors.plugin.postgres_connection_pool;
import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.iaction.IPoorAction;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool.Pool;
import info.smart_tools.smartactors.core.postgres_connection.PostgresConnection;
import info.smart_tools.smartactors.core.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.plugin.postges_connection_pool.PostgresConnectionPoolPlugin;
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.function.Supplier;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyNew;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@PrepareForTest({IOC.class, Keys.class, PostgresConnectionPoolPlugin.class, IPoorAction.class, Pool.class, ApplyFunctionToArgumentsStrategy.class})
@RunWith(PowerMockRunner.class)
public class PostgresConnectionPoolPluginTest {
    private PostgresConnectionPoolPlugin plugin;
    private IBootstrap bootstrap;

    @Before
    public void setUp() throws Exception {

        mockStatic(IOC.class);
        mockStatic(Keys.class);

        bootstrap = mock(IBootstrap.class);
        plugin = new PostgresConnectionPoolPlugin(bootstrap);
    }

    @Test
    public void Should() throws Exception {

        BootstrapItem bootstrapItem = mock(BootstrapItem.class);
        whenNew(BootstrapItem.class).withArguments("PostgresConnectionPoolPlugin").thenReturn(bootstrapItem);
        when(bootstrapItem.after(anyString())).thenReturn(bootstrapItem);
        plugin.load();
        verifyNew(BootstrapItem.class).withArguments("PostgresConnectionPoolPlugin");

        verify(bootstrapItem).after("IOC");
        ArgumentCaptor<IPoorAction> actionArgumentCaptor = ArgumentCaptor.forClass(IPoorAction.class);
        verify(bootstrapItem).process(actionArgumentCaptor.capture());

        IKey postgresConnectionPoolKey = mock(IKey.class);
        when(Keys.getOrAdd("PostgresConnectionPool")).thenReturn(postgresConnectionPoolKey);

        verify(bootstrap).add(eq(bootstrapItem));

        actionArgumentCaptor.getValue().execute();

        verifyStatic();
        Keys.getOrAdd("PostgresConnectionPool");

        ArgumentCaptor<ApplyFunctionToArgumentsStrategy> strategyArgumentCaptor = ArgumentCaptor.forClass(ApplyFunctionToArgumentsStrategy.class);
        verifyStatic();
        IOC.register(eq(postgresConnectionPoolKey), strategyArgumentCaptor.capture());

        ConnectionOptions connectionOptions = mock(ConnectionOptions.class);

        Integer maxConnections = 50;
        when(connectionOptions.getMaxConnections()).thenReturn(maxConnections);

        ArgumentCaptor<Supplier> supplierArgumentCaptor = ArgumentCaptor.forClass(Supplier.class);

        Pool pool = mock(Pool.class);

        whenNew(Pool.class).withArguments(eq(maxConnections), supplierArgumentCaptor.capture()).thenReturn(pool);

        assertTrue(strategyArgumentCaptor.getValue().resolve(connectionOptions) == pool);

        verifyNew(Pool.class).withArguments(eq(maxConnections), supplierArgumentCaptor.capture());

        PostgresConnection connection = mock(PostgresConnection.class);
        whenNew(PostgresConnection.class).withArguments(connectionOptions).thenReturn(connection);

        //TODO: write test for capturing supplier callback from new
//        assertTrue(supplierArgumentCaptor.getValue().get() == connection);
    }
}

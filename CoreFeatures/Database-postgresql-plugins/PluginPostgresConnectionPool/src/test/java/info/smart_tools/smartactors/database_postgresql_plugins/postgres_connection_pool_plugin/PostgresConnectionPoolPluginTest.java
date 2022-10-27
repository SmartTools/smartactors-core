package info.smart_tools.smartactors.database_postgresql_plugins.postgres_connection_pool_plugin;

import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.pool.Pool;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.PostgresConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin.PluginIOCKeys;
import info.smart_tools.smartactors.ioc_plugins.ioc_simple_container_plugin.PluginIOCSimpleContainer;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class PostgresConnectionPoolPluginTest {

    private ConnectionOptions connectionOptions;

    @Before
    public void setUp() throws Exception {
        Bootstrap bootstrap = new Bootstrap();
        new PluginIOCSimpleContainer(bootstrap).load();
        new PluginIOCKeys(bootstrap).load();
        new PostgresConnectionPoolPlugin(bootstrap).load();
        bootstrap.start();

        connectionOptions = mock(ConnectionOptions.class);
        when(connectionOptions.getMaxConnections()).thenReturn(10);
    }

    @Test
    public void testPoolResolve() throws Exception {
        IPool pool = IOC.resolve(Keys.getKeyByName("PostgresConnectionPool"), connectionOptions);
        assertThat(pool, is(instanceOf(Pool.class)));
    }

    @Test
    public void testPoolResolveDatabaseConnection() throws Exception {
        IPool pool = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);
        assertThat(pool, is(instanceOf(Pool.class)));
    }

    @Test
    @Ignore("Requires actual DB connection, need to mock JDBC :(")
    public void testPoolReturnConnection() throws Exception {
        IPool pool = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);
        Object connection = pool.get();
        assertThat(connection, is(instanceOf(PostgresConnection.class)));
    }

    @Test
    public void testResolveSamePool() throws ResolutionException {
        IPool pool1 = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);
        IPool pool2 = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);
        assertSame(pool1, pool2);
    }

    @Test
    public void testResolveOtherPool() throws ResolutionException, ReadValueException {
        IPool pool1 = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions);
        ConnectionOptions connectionOptions2 = mock(ConnectionOptions.class);
        when(connectionOptions2.getMaxConnections()).thenReturn(42);
        IPool pool2 = IOC.resolve(Keys.getKeyByName("DatabaseConnectionPool"), connectionOptions2);
        assertNotSame(pool1, pool2);
    }

}

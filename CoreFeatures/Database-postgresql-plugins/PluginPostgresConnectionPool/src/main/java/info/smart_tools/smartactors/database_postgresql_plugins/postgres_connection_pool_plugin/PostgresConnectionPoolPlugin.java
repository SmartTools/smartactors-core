package info.smart_tools.smartactors.database_postgresql_plugins.postgres_connection_pool_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipool.IPool;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.pool.Pool;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.database.interfaces.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.PostgresConnection;
import info.smart_tools.smartactors.database_postgresql.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin.
 * Implements {@link IPlugin}
 * Load connection pool for postgres.
 */
public class PostgresConnectionPoolPlugin implements IPlugin {

    /** Local storage for instance of {@link IBootstrap}*/
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor with single argument
     * @param bootstrap instance of {@link IBootstrap}
     * @throws InvalidArgumentException if any errors occurred
     */
    public PostgresConnectionPoolPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap)
            throws InvalidArgumentException {
        if (null == bootstrap) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            BootstrapItem item = new BootstrapItem("PostgresConnectionPoolPlugin");
            item
//                .after("IOC")
//                .after("ioc_keys")
                .before("CreateCachedCollectionPlugin")
                .process(() -> {
                    try {
                        IStrategy poolStrategy = new ApplyFunctionToArgumentsStrategy(
                                (args) -> {
                                    ConnectionOptions connectionOptions = (ConnectionOptions) args[0];

                                    final IKey poolKey;
                                    try {
                                        poolKey = getPoolKey(connectionOptions);
                                    } catch (Exception e) {
                                        throw new RuntimeException("Can't build the pool key: connectionOptions = " + connectionOptions, e);
                                    }

                                    try {
                                        return IOC.resolve(poolKey);
                                    } catch (ResolutionException re) {      // pool not found
                                        try {
                                            IPool pool = new Pool(
                                                connectionOptions.getMaxConnections(),
                                                () -> {
                                                    try {
                                                        return new PostgresConnection(connectionOptions);
                                                    } catch (StorageException se) {
                                                        throw new RuntimeException(
                                                                "Cannot create PostgresConnection: poolKey = " + poolKey, se);
                                                    }
                                                },
                                                (connection) -> {
                                                    try {
                                                        return ((PostgresConnection) connection).validate();
                                                    } catch (StorageException se) {
                                                        throw new RuntimeException(
                                                                "Cannot validate PostgresConnection: poolKey = " + poolKey, se);
                                                    }
                                                }
                                            );
                                            IOC.register(poolKey, new SingletonStrategy(pool));
                                            return pool;
                                        } catch (Exception e) {
                                            throw new RuntimeException("Can't create PostgresConnectionPool: poolKey = " + poolKey, e);
                                        }
                                    }
                                });
                        IKey postgresConnectionPoolKey = Keys.getKeyByName("PostgresConnectionPool");
                        IKey databaseConnectionPoolKey = Keys.getKeyByName("DatabaseConnectionPool");
                        IOC.register(postgresConnectionPoolKey, poolStrategy);
                        IOC.register(databaseConnectionPoolKey, poolStrategy);
                    } catch (Exception e) {
                        throw new ActionExecutionException(
                                "PostgresConnectionPool plugin can't load", e);
                    }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load postgres connection pool plugin", e);
        }
    }

    /**
     * Calculates the pool key from the connection options
     * to resolve the same pool when it's requested.
     * Note, the password is not used to construct the key.
     * @param options connection options to construct the key from
     * @return the IOC key to store the pool in
     */
    private IKey getPoolKey(final ConnectionOptions options) throws ReadValueException, ResolutionException {
        return Keys.getKeyByName(String.format("postgres_connection_%s_%s_%d",
                options.getUrl(), options.getUsername(), options.getMaxConnections()));
    }

}

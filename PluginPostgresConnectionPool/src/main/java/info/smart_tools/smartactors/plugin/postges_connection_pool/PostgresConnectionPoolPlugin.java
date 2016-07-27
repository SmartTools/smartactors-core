package info.smart_tools.smartactors.plugin.postges_connection_pool;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.istorage_connection.exception.StorageException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.pool.Pool;
import info.smart_tools.smartactors.core.postgres_connection.PostgresConnection;
import info.smart_tools.smartactors.core.postgres_connection.wrapper.ConnectionOptions;
import info.smart_tools.smartactors.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;

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
            IBootstrapItem<String> item = new BootstrapItem("PostgresConnectionPoolPlugin");
            item
                .after("IOC")
                .process(() -> {
                    try {
                        IKey postgresConnectionPoolKey = Keys.getOrAdd("PostgresConnectionPool");
                        IOC.register(postgresConnectionPoolKey, new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                ConnectionOptions connectionOptions = (ConnectionOptions) args[0];
                                if (connectionOptions == null) {
                                    throw new RuntimeException("Can't resolve connection pool: connectionOptions is null");
                                }

                                try {
                                    return new Pool(connectionOptions.getMaxConnections(), () -> {
                                        try {
                                            return new PostgresConnection(connectionOptions);
                                        } catch (StorageException e) {
                                            throw new RuntimeException(e);
                                        }
                                    });
                                } catch (ReadValueException e) {
                                    throw new RuntimeException("Can't create PostgresConnectionPool", e);
                                }

                            }));
                    } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                        throw new RuntimeException(e);
                    }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load postgres connection pool plugin", e);
        }
    }
}

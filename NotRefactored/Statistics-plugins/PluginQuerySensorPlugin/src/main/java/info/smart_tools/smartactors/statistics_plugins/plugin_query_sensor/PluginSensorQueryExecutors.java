package info.smart_tools.smartactors.statistics_plugins.plugin_query_sensor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.query_executors.DatabaseCountQueryExecutor;

/**
 *
 */
public class PluginSensorQueryExecutors extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginSensorQueryExecutors(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register the {@link info.smart_tools.smartactors.statistics.sensors.scheduled_query_sensor.interfaces.IQueryExecutor sensor query
     * executor} that queries count of documents in a database collection.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies of query executor
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if the executor is not accepted by {@link SingletonStrategy}
     */
    @Item("sensor_query_executor:database_count")
    public void registerDatabaseCountQueryExecutor()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("database count sensor query executor"), new SingletonStrategy(new DatabaseCountQueryExecutor()));
    }
}

package info.smart_tools.smartactors.statistics.sensors.embedded_sensor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies.CountStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.strategies.TimeDeltaForLimitedCountStrategy;

/**
 * Plugin that registers some implementations of {@link
 * info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy embedded sensor strategy}.
 */
public class PluginEmbeddedSensorStrategies extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginEmbeddedSensorStrategies(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Registers a strategy resolving an embedded sensor strategy that records count of messages received by a receiver.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link SingletonStrategy} does not accept our strategy
     */
    @Item("embedded_sensor_strategy:count")
    public void registerCountStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("embedded sensor count strategy"), new SingletonStrategy(new CountStrategy()));
    }

    /**
     * Registers a strategy resolving an embedded sensor strategy that records time delta for limited count of messages at every period.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not accept our strategy
     */
    @Item("embedded_sensor_strategy:limited_count_time_delta")
    public void registerTimeDeltaStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IFieldName limitFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "limit");
        IFieldName timeFieldNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "timeFieldName");
        IOC.register(Keys.getKeyByName("embedded sensor time delta strategy for limited count"), new ApplyFunctionToArgumentsStrategy(args -> {
            IObject arg = (IObject) args[0];

            try {
                int limit = ((Number) arg.getValue(limitFieldName)).intValue();
                IFieldName timeFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        arg.getValue(timeFieldNameFieldName));
                return new TimeDeltaForLimitedCountStrategy(limit, timeFieldName);
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

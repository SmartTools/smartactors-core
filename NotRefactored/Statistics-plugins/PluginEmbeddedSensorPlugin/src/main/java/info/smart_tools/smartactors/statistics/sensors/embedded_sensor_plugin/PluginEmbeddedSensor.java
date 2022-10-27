package info.smart_tools.smartactors.statistics.sensors.embedded_sensor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.EmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.EmbeddedSensorReceiver;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorObservationPeriod;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.IEmbeddedSensorStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.interfaces.exceptions.EmbeddedSensorStrategyException;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils.EmbeddedSensorCreationStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils.PrependSensorReceiverStrategy;
import info.smart_tools.smartactors.statistics.sensors.embedded_sensor.utils.SaveTimestampReceiver;

/**
 * Plugin that registers a strategy of creation of embedded sensor and some related strategies.
 */
public class PluginEmbeddedSensor extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginEmbeddedSensor(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register the strategy creating embedded sensors.
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws ResolutionException if error occurs resolving any dependency of the strategy
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("embedded_sensor_creation_strategy")
    @After({"prepend_sensor_receiver_strategy", "embedded_sensor_receiver_strategy"})
    public void registerCreationStrategy()
            throws ResolutionException, RegistrationException {
        IOC.register(Keys.getKeyByName("create embedded sensor"), new EmbeddedSensorCreationStrategy());
    }

    /**
     * Register strategy creating a receiver for embedded sensor.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not accept our strategy
     */
    @Item("embedded_sensor_receiver_strategy")
    @After({"embedded_sensor_observation_period_creation_strategy"})
    public void registerEmbeddedSensorReceiverCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("embedded sensor receiver"), new ApplyFunctionToArgumentsStrategy(args -> {
            IObject arg = (IObject) args[0];

            try {
                return new EmbeddedSensorReceiver(arg);
            } catch (ResolutionException | ReadValueException | EmbeddedSensorStrategyException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    private static final int ESOP_START_ARG_INDEX = 0;
    private static final int ESOP_END_ARG_INDEX = 1;
    private static final int ESOP_MAX_ITEMS_ARG_INDEX = 2;
    private static final int ESOP_STRATEGY_ARG_INDEX = 3;

    /**
     * Register strategy creating a new instance of {@link EmbeddedSensorObservationPeriod}.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not accept our strategy
     */
    @Item("embedded_sensor_observation_period_creation_strategy")
    public void registerEmbeddedSensorObservationPeriodCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {

        IOC.register(Keys.getKeyByName(IEmbeddedSensorObservationPeriod.class.getCanonicalName()),
                new ApplyFunctionToArgumentsStrategy(args -> {
            long start = ((Number) args[ESOP_START_ARG_INDEX]).longValue();
            long end = ((Number) args[ESOP_END_ARG_INDEX]).longValue();
            long maxItems = ((Number) args[ESOP_MAX_ITEMS_ARG_INDEX]).longValue();
            IEmbeddedSensorStrategy<?> strategy = (IEmbeddedSensorStrategy<?>) args[ESOP_STRATEGY_ARG_INDEX];

            try {
                return new EmbeddedSensorObservationPeriod<>(start, end, maxItems, strategy);
            } catch (ResolutionException | EmbeddedSensorStrategyException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register the strategy creating a composite receiver.
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("prepend_sensor_receiver_strategy")
    public void registerSensorPrependStrategy()
            throws ResolutionException, RegistrationException {
        IOC.register(Keys.getKeyByName("prepend sensor receiver"), new PrependSensorReceiverStrategy());
    }

    /**
     * Register the strategy creating new instance of {@link SaveTimestampReceiver} from {@link IObject} alike to:
     *
     * <pre>
     *     {
     *         "timeFieldName": "field_where_time_is",
     *         ...
     *     }
     * </pre>
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not accept our strategy
     */
    @Item("save_timestamp_receiver_creation_strategy")
    public void registerTimesatampSaverCreationStrategy()
            throws RegistrationException, ResolutionException, InvalidArgumentException {
        IFieldName timeFieldNameFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "timeFieldName");

        IOC.register(Keys.getKeyByName("save timestamp receiver"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                IObject arg = (IObject) args[0];
                IFieldName timeFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                        arg.getValue(timeFieldNameFieldName));

                return new SaveTimestampReceiver(timeFieldName);
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

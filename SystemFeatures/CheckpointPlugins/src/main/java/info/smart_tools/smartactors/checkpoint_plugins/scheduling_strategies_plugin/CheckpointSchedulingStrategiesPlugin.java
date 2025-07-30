package info.smart_tools.smartactors.checkpoint_plugins.scheduling_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.checkpoint.scheduling_strategies.CheckpointFibonacciRepeatStrategy;
import info.smart_tools.smartactors.checkpoint.scheduling_strategies.CheckpointRegularRepeatStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin that registers some scheduling strategies useful for checkpoint configuration.
 */
public class CheckpointSchedulingStrategiesPlugin extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CheckpointSchedulingStrategiesPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register regular repeat strategy.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies
     * @throws InvalidArgumentException if some unexpected error occurs
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("checkpoint_scheduling_strategy:regular_repeat")
    @Before("checkpoint_actor")
    public void registerRegularRepeatStrategy()
            throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(Keys.getKeyByName("checkpoint repeat strategy"),
                new SingletonStrategy(new CheckpointRegularRepeatStrategy()));
    }

    /**
     * Register repeat with intervals proportional to Fibonacci numbers strategy.
     *
     * @throws ResolutionException if error occurs resolving key or dependencies
     * @throws InvalidArgumentException if some unexpected error occurs
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("checkpoint_scheduling_strategy:fibonacci_repeat")
    @Before("checkpoint_actor")
    public void registerFibonacciRepeatStrategy()
            throws ResolutionException, InvalidArgumentException, RegistrationException {
        IOC.register(Keys.getKeyByName("checkpoint fibonacci repeat strategy"),
                new SingletonStrategy(new CheckpointFibonacciRepeatStrategy()));
    }
}

package info.smart_tools.smartactors.scheduler_plugins.scheduling_strategies_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.scheduler.strategies.ContinuouslyRepeatScheduleStrategy;
import info.smart_tools.smartactors.scheduler.strategies.OnceSchedulingStrategy;

/**
 * Plugin that registers some scheduling strategies.
 */
public class PluginSchedulingStrategies extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginSchedulingStrategies(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategies.
     *
     * @throws ResolutionException if error occurs resolving the keys
     * @throws RegistrationException if error occurs registering the strategies in IOC
     * @throws InvalidArgumentException if {@link SingletonStrategy} hates {@link
     *                                  info.smart_tools.smartactors.scheduler.interfaces.ISchedulingStrategy scheduling strategies}
     */
    @Item("scheduling_strategies")
    @After({
            "core",
    })
    @Before({"scheduler_actor"})
    public void registerSchedulingStrategies()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("repeat continuously scheduling strategy"),
                new SingletonStrategy(new ContinuouslyRepeatScheduleStrategy()));

        IOC.register(
                Keys.getKeyByName("do once scheduling strategy"),
                new SingletonStrategy(new OnceSchedulingStrategy()));
    }
}

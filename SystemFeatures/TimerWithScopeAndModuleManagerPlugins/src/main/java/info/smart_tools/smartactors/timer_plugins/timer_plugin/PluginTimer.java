package info.smart_tools.smartactors.timer_plugins.timer_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.timer.interfaces.itimer.ITimer;
import info.smart_tools.smartactors.timer.timer.SystemTimeImpl;
import info.smart_tools.smartactors.timer.timer.TimerImpl;

import java.util.Timer;

/**
 * Plugin that creates and registers in IOC an instance of {@link ITimer}.
 */
public class PluginTimer implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     *
     * @param bootstrap the bootstrap
     */
    public PluginTimer(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("timer");

            item.process(() -> {
                try {
                    IOC.register(Keys.getKeyByName("timer"), new SingletonStrategy(
                            new TimerImpl(new Timer("Smart actors system timer", true))));
                } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                    throw new ActionExecutionException(e);
                }
            })
            .revertProcess(() -> {
                String[] itemNames = { "timer" };
                Keys.unregisterByNames(itemNames);
            });

            bootstrap.add(item);

            item = new BootstrapItem("time");

            item.process(() -> {
                try {
                    IOC.register(Keys.getKeyByName("time"), new SingletonStrategy(new SystemTimeImpl()));
                } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                    throw new ActionExecutionException(e);
                }
            })
            .revertProcess(() -> {
                String[] itemNames = { "time" };
                Keys.unregisterByNames(itemNames);
            });

            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

package info.smart_tools.smartactors.system_actors_pack_plugins.repeater_actor_plugin;

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
import info.smart_tools.smartactors.system_actors_pack.repeater_actor.RepeaterActor;

/**
 * Plugin that registers {@link RepeaterActor}.
 */
public class RepeaterActorPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     * @param bootstrap the bootstrap
     */
    public RepeaterActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("actor:repeater");

            item
//                    .after("IOC")
//                    .before("configure")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName("RepeaterActor"),
                                    new SingletonStrategy(new RepeaterActor()));
                        } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    });

            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

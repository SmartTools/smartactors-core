package info.smart_tools.smartactors.endpoint_plugins.stop_endpoint_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.actor.stop_endpoint.StopEndpointActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin for actor, that stopping endpoint
 */
public class PluginStopEndpointActor implements IPlugin {

    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap the bootstrap
     */
    public PluginStopEndpointActor(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }


    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> responseSenderItem = new BootstrapItem("actor:stop_endpoint");

            responseSenderItem
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd("StopEndpointActor"),
                                    // Response sender is stateless so it's safe to use singleton strategy.
                                    new SingletonStrategy(new StopEndpointActor()));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("StopEndpointActor plugin can't load: can't get StopEndpointActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("StopEndpointActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("StopEndpointActor plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(responseSenderItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

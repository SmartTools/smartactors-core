package infol.smart_tools.smartactors.endpoint_plugins;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.endpoint.actor.start_endpoint.StartEndpointActor;
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
 * Plugin for actor, that starting endpoint
 */
public class PluginStartEndpoint implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap the bootstrap
     */
    public PluginStartEndpoint(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> responseSenderItem = new BootstrapItem("actor:start_endpoint");

            responseSenderItem
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd("StartEndpointActor"),
                                    new SingletonStrategy(new StartEndpointActor()));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("StartEndpointActor plugin can't load: can't get StartEndpointActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("StartEndpointActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("StartEndpointActor plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(responseSenderItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

package info.smart_tools.smartactors.https_endpoint_plugins.https_request_sender_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.https_endpoint.https_request_sender_actor.HttpsRequestSenderActor;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Created by sevenbits on 15.10.16.
 */
public class PluginHttpsRequestSenderActor implements IPlugin {

    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap    the bootstrap
     */
    public PluginHttpsRequestSenderActor(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> requestSenderItem = new BootstrapItem("actor:https_request_sender");

            requestSenderItem
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName("HttpsRequestSenderActor"),
                                    // Response sender is stateless so it's safe to use singleton strategy.
                                    new SingletonStrategy(new HttpsRequestSenderActor()));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("RequestSenderActor plugin can't load: can't get RequestSenderActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("RequestSenderActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("RequestSenderActor plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(requestSenderItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

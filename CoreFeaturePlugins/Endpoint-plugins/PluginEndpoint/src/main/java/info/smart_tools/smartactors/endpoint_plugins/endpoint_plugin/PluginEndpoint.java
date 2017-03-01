package info.smart_tools.smartactors.endpoint_plugins.endpoint_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.endpoint.interfaces.iasync_service.IAsyncService;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by sevenbits on 12.11.16.
 */
public class PluginEndpoint implements IPlugin {

    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap the bootstrap
     */
    public PluginEndpoint(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }


    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> responseSenderItem = new BootstrapItem("endpointPlugin");

            responseSenderItem
//                    .after("IOC")
//                    .before("starter")
                    .process(() -> {
                        try {
                            Map<String, IAsyncService> endpoints = new HashMap<String, IAsyncService>();
                            IOC.register(
                                    Keys.getOrAdd("endpoints"),
                                    // Response sender is stateless so it's safe to use singleton strategy.
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> {
                                                if (args.length == 1) {
                                                    return endpoints.get(args[0]);
                                                }
                                                if (endpoints.containsKey(args[0])) {
                                                    throw new RuntimeException("Failed to add endpoint, endpoint with this name already exist");
                                                }
                                                endpoints.put((String) args[0], (IAsyncService) args[1]);
                                                return endpoints.get(args[0]);
                                            }
                                    ));
                            IOC.register(Keys.getOrAdd("removeEndpoint"),
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> {
                                                if (!endpoints.containsKey(args[0])) {
                                                    throw new RuntimeException("Failed to remove endpoint, endpoint with this name doesn`t exist");
                                                }
                                                endpoints.remove((String) args[0]);
                                                return true;
                                            }
                                    ));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("ResponseSenderActor plugin can't load: can't get ResponseSenderActor key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("ResponseSenderActor plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("ResponseSenderActor plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(responseSenderItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

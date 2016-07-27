package info.smart_tools.smartactors.plugin.authentication;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Plugin for Authentication actor
 */
public class AuthenticationActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public AuthenticationActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Load the plugin for Authentication actor
     * @throws PluginException Throw when plugin can't be load
     */
    @Override
    public void load() throws PluginException {
        try {
            IKey cachedCollectionKey = Keys.getOrAdd(AuthenticationActor.class.toString());
            IBootstrapItem<String> item = new BootstrapItem("AuthenticationActorPlugin");

            item.after("IOC").process(() -> {
                try {
                    IOC.register(cachedCollectionKey, new CreateNewInstanceStrategy(
                            (args) -> {
                                try {
                                    return new AuthenticationActor();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                } catch (RegistrationException | InvalidArgumentException e) {
                    throw new ActionExecuteException(e);
                }
            });
            bootstrap.add(item);
        } catch (ResolutionException | InvalidArgumentException e) {
            throw new PluginException("Can't load AuthenticationActor plugin", e);
        }
    }
}

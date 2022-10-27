package info.smart_tools.smartactors.plugin.exception_handler_actor;

import info.smart_tools.smartactors.actor.exception_handler_actor.ExceptionHandlerActor;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 * Plugin for register {@link ExceptionHandlerActor} creation strategy
 */
public class ExceptionHandlerActorPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public ExceptionHandlerActorPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    /**
     * Load the plugin for {@link ExceptionHandlerActor}
     * @throws PluginException Throw when plugin can't be load
     */
    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("ExceptionHandlerActorPlugin");

            item
                    .after("IOC")
                    .process(() -> {
                try {
                    IOC.register(Keys.getKeyByName("ExceptionHandlerActor"), new ApplyFunctionToArgumentsStrategy(
                            (args) -> {
                                try {
                                    return new ExceptionHandlerActor();
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                } catch (ResolutionException | RegistrationException | InvalidArgumentException e) {
                    throw new ActionExecutionException(e);
                }
            });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load ExceptionHandlerActor plugin", e);
        }
    }
}

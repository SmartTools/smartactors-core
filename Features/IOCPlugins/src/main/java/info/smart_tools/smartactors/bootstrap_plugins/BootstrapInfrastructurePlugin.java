package info.smart_tools.smartactors.bootstrap_plugins;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

public class BootstrapInfrastructurePlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public BootstrapInfrastructurePlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register bootstrap infrastructure.
     *
     * @throws ResolutionException if error occurs resolving key or strategy dependencies
     * @throws RegistrationException if error occurs registering the strategy
     */
    @Item("bootstrapInfrastructure")
    @After({
            "IOC",
    })
    @SuppressWarnings("unchecked")
    public void registerBootstrapInfrastructureStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(
                Keys.getKeyByName("bootstrap item"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new BootstrapItem((String) args[0])
                )
        );
        IOC.register(
                Keys.getKeyByName("bootstrap"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> new Bootstrap()
                )
        );
        IOC.register(Keys.getKeyByName("plugin creator"), new SingletonStrategy(new PluginCreator()));
        IOC.register(Keys.getKeyByName("plugin loader visitor"), new SingletonStrategy(new PluginLoaderVisitor<String>()));
        IOC.register(Keys.getKeyByName("plugin loader"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new PluginLoader(
                        (ISmartactorsClassLoader) args[0], (IAction<Class>) args[1], (IPluginLoaderVisitor) args[2]);
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }));

    }

    /**
     * Unregisters bootstrap infrastructure.
     */
    @ItemRevert("bootstrapInfrastructure")
    public void unregisterBootstrapInfrastructureStrategy() {
        String[] keyNames = {
                "plugin loader",
                "plugin loader visitor",
                "plugin creator",
                "bootstrap",
                "bootstrap item",
        };
        Keys.unregisterByNames(keyNames);
    }
}

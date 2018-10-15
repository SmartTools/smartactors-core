package info.smart_tools.smartactors.ioc_strategy_pack_plugins.datetime_formatter_strategy_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.time.format.DateTimeFormatter;

/**
 * Plugin for registration strategy for date time formatting.
 * Strategy resolves {@link DateTimeFormatter} by MM-dd-yyyy HH:mm:ss pattern.
 */
public class PluginDateTimeFormatter implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     * @param bootstrap the bootstrap
     */
    public PluginDateTimeFormatter(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            IBootstrapItem<String> bootstrapItem = new BootstrapItem("datetime_formatter_plugin");

            bootstrapItem
                .after("IOC")
                .process(() -> {
                    try {
                        IOC.register(Keys.getOrAdd("datetime_formatter"),
                            new ApplyFunctionToArgumentsStrategy(args -> DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"))
                        );
                    } catch (ResolutionException e) {
                        throw new ActionExecuteException("DateTimeFormatter plugin can't load: can't get DateTimeFormatter key", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecuteException("DateTimeFormatter plugin can't load: can't create strategy", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecuteException("DateTimeFormatter plugin can't load: can't register new strategy", e);
                    }
                })
                .revertProcess(() -> {
                    String itemName = "datetime_formatter_plugin";
                    String keyName = "";

                    try {
                        keyName = "datetime_formatter";
                        IOC.remove(Keys.getOrAdd(keyName));
                    } catch(DeletionException e) {
                        System.out.println("[WARNING] Deregitration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
                    } catch (ResolutionException e) { }
                });
            bootstrap.add(bootstrapItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

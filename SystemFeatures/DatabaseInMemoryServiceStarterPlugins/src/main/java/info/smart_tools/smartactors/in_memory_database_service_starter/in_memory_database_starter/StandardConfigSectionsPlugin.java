package info.smart_tools.smartactors.in_memory_database_service_starter.in_memory_database_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class StandardConfigSectionsPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public StandardConfigSectionsPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            /* "messageBus" section */
            IBootstrapItem<String> inMemoryDatabaseItem = new BootstrapItem("config_section:inMemoryDb");

            inMemoryDatabaseItem
//                    .after("config_sections:start")
//                    .before("config_sections:done")
//                    .after("config_section:maps")
//                    .after("config_section:executor")
//                    .after("IFieldNamePlugin")
//                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new InMemoryDBSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecutionException(e);
                        }
                    });
            bootstrap.add(inMemoryDatabaseItem);
            IOC.register(Keys.getKeyByName("IObjectByString"), new ApplyFunctionToArgumentsStrategy(
                            (args) ->
                                    new DSObject((String) args[0])
                    )
            );
        } catch (InvalidArgumentException | RegistrationException | ResolutionException e) {
            throw new PluginException(e);
        }
    }
}

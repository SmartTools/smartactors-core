package info.smart_tools.smartactors.core_service_starter.core_starter;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

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
            /* "objects" section */
            IBootstrapItem<String> objectsSectionItem = new BootstrapItem("config_section:objects");

            objectsSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("router")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new ObjectsSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(objectsSectionItem);

            /* "maps" section */
            IBootstrapItem<String> mapsSectionItem = new BootstrapItem("config_section:maps");

            mapsSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("config_section:objects")
                    .after("receiver_chains_storage")
                    .after("receiver_chain")
                    .after("IFieldNamePlugin")
                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new MapsSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(mapsSectionItem);

            /* "executor" section */
            IBootstrapItem<String> executorSectionItem = new BootstrapItem("config_section:executor");

            executorSectionItem
                    .after("config_sections:start")
                    .before("config_sections:done")
                    .after("queue")
                    .after("IFieldNamePlugin")
                    .after("root_upcounter")
                    .before("starter")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new ExecutorSectionProcessingStrategy());
                        } catch (ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(executorSectionItem);

        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

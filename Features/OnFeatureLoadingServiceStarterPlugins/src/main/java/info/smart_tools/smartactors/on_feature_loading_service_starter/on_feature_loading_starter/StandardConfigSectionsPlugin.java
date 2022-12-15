package info.smart_tools.smartactors.on_feature_loading_service_starter.on_feature_loading_starter;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.IStrategyRegistration;
import info.smart_tools.smartactors.base.interfaces.istrategy_registration.exception.StrategyRegistrationException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.List;

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
            /* "onFeatureLoading" section */
            IBootstrapItem<String> onFeatureLoadingItem = new BootstrapItem("config_section:onFeatureLoading");
            onFeatureLoadingItem
                    .after("config_sections:done")
                    .process(() -> {
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

                            configurationManager.addSectionStrategy(new OnFeatureLoadingSectionProcessingStrategy());
                            IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));

                            strategy.register("onFeatureLoading", new ApplyFunctionToArgumentsStrategy(args -> {
                                try {
                                    Object section = args[1];

                                    if (section instanceof List) {
                                        for (IObject item : (List<IObject>) section) {
                                            IFieldName revertFN = IOC.resolve(Keys.getKeyByName(IFieldName.class.getCanonicalName()), "revert");
                                            if (null == item.getValue(revertFN)) {
                                                item.setValue(revertFN, false);
                                            }
                                        }
                                    }
                                    return section;
                                } catch (Throwable e) {
                                    throw new RuntimeException("Error in configuration 'canonical maps' rule.", e);
                                }
                            }));
                        } catch (ResolutionException | InvalidArgumentException | StrategyRegistrationException e) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IStrategyRegistration strategy = IOC.resolve(Keys.getKeyByName("expandable_strategy#resolve key for configuration object"));
                            try {
                                strategy.unregister("onFeatureLoading");
                            } catch (StrategyRegistrationException e) {
                                System.out.println("[\033[1;33mWARNING\033[0m] Deregistration of \"onFeatureLoading\" strategy has failed while reverting \"config_section:onFeatureLoading\" plugin.");
                            }
                        } catch (ResolutionException e) {
                            // ToDo: !!! Unhandled catch
                        }
                        try {
                            IConfigurationManager configurationManager =
                                    IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
                            ISectionStrategy sectionStrategy = new OnFeatureLoadingSectionProcessingStrategy();
                            configurationManager.removeSectionStrategy(sectionStrategy.getSectionName());
                        } catch ( InvalidArgumentException e) {
                            System.out.println("[\033[1;33mWARNING\033[0m] Deregistration of \"OnFeatureLoadingSectionProcessingStrategy\" has failed while reverting \"config_section:onFeatureLoading\" plugin.");
                        } catch (ResolutionException e) {
                            // ToDo: !!! Unhandled catch
                        }
                    });
            bootstrap.add(onFeatureLoadingItem);
        } catch (Exception e) {
            throw new PluginException(e);
        }
    }
}

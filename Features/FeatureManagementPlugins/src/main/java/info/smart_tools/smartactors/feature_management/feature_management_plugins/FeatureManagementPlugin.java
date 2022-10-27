package info.smart_tools.smartactors.feature_management.feature_management_plugins;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.feature_management.after_features_callback_storage.AfterFeaturesCallbackStorage;
import info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.AllInDirectoryFeatureTracker;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.RuntimeDirectoryFeatureTracker;
import info.smart_tools.smartactors.feature_management.download_feature_actor.DownloadFeatureActor;
import info.smart_tools.smartactors.feature_management.feature_creator_actor.FeaturesCreatorActor;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor;
import info.smart_tools.smartactors.feature_management.load_feature_actor.LoadFeatureActor;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.UnzipFeatureActor;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.ArrayList;

/**
 * Plugin registers needed strategies to the IOC
 */
public class FeatureManagementPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     * @param bootstrap the bootstrap
     */
    public FeatureManagementPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) throws Exception {
        this.bootstrap = bootstrap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("feature_management");

            item
                    .after("IOC")
                    .after("configuration_manager")
                    .after("config_sections:done")
                    .after("IFieldNamePlugin")
                    .after("ConfigurationObject")
                    .after("config_section:onFeatureLoading")
                    .after("bootstrapInfrastructure")
                    .before("read_initial_config")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getKeyByName("feature-repositories"), new SingletonStrategy(
                                    new ArrayList<IObject>()
                            ));

                            IOC.register(Keys.getKeyByName("FeatureManager"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new FeatureManagerActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(
                                    Keys.getKeyByName("feature group load completion task queue"),
                                    new ApplyFunctionToArgumentsStrategy(
                                            args -> AfterFeaturesCallbackStorage.getLocalCallbackQueue()
                                    )
                            );
                            IOC.register(Keys.getKeyByName("DownloadFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new DownloadFeatureActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(Keys.getKeyByName("UnzipFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new UnzipFeatureActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(Keys.getKeyByName("LoadFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new LoadFeatureActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(Keys.getKeyByName("AllInDirectoryFeatureTracker"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new AllInDirectoryFeatureTracker();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(Keys.getKeyByName("DirectoryWatcherActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new RuntimeDirectoryFeatureTracker();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                            );
                            IOC.register(Keys.getKeyByName("FeatureCreatorActor"), new ApplyFunctionToArgumentsStrategy(
                                    (args) -> {
                                        try {
                                            return new FeaturesCreatorActor();
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    })
                            );
                        } catch (ResolutionException | InvalidArgumentException | RegistrationException e ) {
                            throw new ActionExecutionException(e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = {
                                "FeatureCreatorActor",
                                "DirectoryWatcherActor",
                                "DirectoryWatcherActor",
                                "LoadFeatureActor",
                                "UnzipFeatureActor",
                                "DownloadFeatureActor",
                                "feature group load completion task queue",
                                "FeatureManager",
                                "feature-repositories"
                        };
                        Keys.unregisterByNames(keyNames);
                    });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}
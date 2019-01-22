package info.smart_tools.smartactors.feature_management.feature_management_plugins;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;
import info.smart_tools.smartactors.feature_management.after_features_callback_storage.AfterFeaturesCallbackStorage;
import info.smart_tools.smartactors.feature_management.all_in_direcory_feature_tracker.AllInDirectoryFeatureTracker;
import info.smart_tools.smartactors.feature_management.directory_watcher_actor.RuntimeDirectoryFeatureTracker;
import info.smart_tools.smartactors.feature_management.download_feature_actor.DownloadFeatureActor;
import info.smart_tools.smartactors.feature_management.feature_creator_actor.FeaturesCreatorActor;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.FeatureManagerActor;
import info.smart_tools.smartactors.feature_management.load_feature_actor.LoadFeatureActor;
import info.smart_tools.smartactors.feature_management.unzip_feature_actor.UnzipFeatureActor;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.DeletionException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;

import java.util.ArrayList;

/**
 * Plugin registers needed strategies to the IOC
 */
public class FeatureManagementPlugin extends BootstrapPlugin {

    /**
     * Creates instance of {@link FeatureManagementPlugin} by specific arguments
     * @param bootstrap the instance of {@link IBootstrap}
     */
    public FeatureManagementPlugin(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Registers dependencies to the IOC
     * @throws ResolutionException if any errors occurred on IOC resolution
     * @throws RegistrationException if any errors occurred on registration dependency to the IOC
     * @throws InvalidArgumentException if any errors occurred on creation objects
     * @throws ChainNotFoundException if any errors occurred on looking for chain
     * @throws ChangeValueException if any errors occurred on writing to the {@link IObject}
     */
    @Item("feature_management")
    @After({"IOC", "configuration_manager", "config_sections:done", "IFieldNamePlugin", "ConfigurationObject", "config_section:onFeatureLoading"})
    @Before("read_initial_config")
    public void register()
            throws ResolutionException, RegistrationException, InvalidArgumentException, ChainNotFoundException, ChangeValueException {

        IOC.register(Keys.resolveByName("plugin creator"), new SingletonStrategy(new PluginCreator()));
        IOC.register(Keys.resolveByName("plugin loader visitor"), new SingletonStrategy(new PluginLoaderVisitor<String>()));
        IOC.register(Keys.resolveByName("plugin loader"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new PluginLoader(
                        (ISmartactorsClassLoader) args[0], (IAction<Class>) args[1], (IPluginLoaderVisitor) args[2]);
            } catch (InvalidArgumentException e) {
                throw new RuntimeException(e);
            }
        }));

        IOC.register(Keys.resolveByName("feature-repositories"), new SingletonStrategy(
                new ArrayList<IObject>()
        ));

        IOC.register(Keys.resolveByName("FeatureManager"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new FeatureManagerActor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        IOC.register(
                Keys.resolveByName("feature group load completion task queue"),
                new ApplyFunctionToArgumentsStrategy(
                        args -> AfterFeaturesCallbackStorage.getLocalCallbackQueue()
                )
        );
        IOC.register(Keys.resolveByName("DownloadFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new DownloadFeatureActor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        IOC.register(Keys.resolveByName("UnzipFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new UnzipFeatureActor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        IOC.register(Keys.resolveByName("LoadFeatureActor"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new LoadFeatureActor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        IOC.register(Keys.resolveByName("AllInDirectoryFeatureTracker"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new AllInDirectoryFeatureTracker();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }));
        IOC.register(Keys.resolveByName("DirectoryWatcherActor"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new RuntimeDirectoryFeatureTracker();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
        IOC.register(Keys.resolveByName("FeatureCreatorActor"), new ApplyFunctionToArgumentsStrategy(
                (args) -> {
                    try {
                        return new FeaturesCreatorActor();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
        );
    }

    @ItemRevert("feature_management")
    public void unregister() {
        String itemName = "feature_management";
        String keyName = "";

        try {
            keyName = "FeatureCreatorActor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "DirectoryWatcherActor";
            RuntimeDirectoryFeatureTracker runtimeDirectoryFeatureTracker = IOC.resolve(Keys.resolveByName(keyName));
            runtimeDirectoryFeatureTracker.stopService(null);
        } catch(Throwable e) {
            System.out.println("[WARNING] \""+keyName+"\" stopping has failed while reverting \""+itemName+"\" plugin.");
        }

        try {
            keyName = "DirectoryWatcherActor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "LoadFeatureActor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "UnzipFeatureActor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "DownloadFeatureActor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "feature group load completion task queue";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "FeatureManager";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "feature-repositories";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "plugin loader";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "plugin loader visitor";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }

        try {
            keyName = "plugin creator";
            IOC.unregister(Keys.resolveByName(keyName));
        } catch(DeletionException e) {
            System.out.println("[WARNING] Deregistration of \""+keyName+"\" has failed while reverting \""+itemName+"\" plugin.");
        } catch (ResolutionException e) { }
    }
}

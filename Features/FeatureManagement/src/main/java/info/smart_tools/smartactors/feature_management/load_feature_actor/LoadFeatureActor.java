package info.smart_tools.smartactors.feature_management.load_feature_actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.exception.PluginCreationException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.feature_management.after_features_callback_storage.AfterFeaturesCallbackStorage;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.feature_management.load_feature_actor.exception.LoadFeatureException;
import info.smart_tools.smartactors.feature_management.load_feature_actor.wrapper.LoadFeatureWrapper;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;
import java.util.stream.Stream;

/**
 * Actor that loads feature classes to the classloader,
 * runs feature plugins and executes feature config
 */
public class LoadFeatureActor {


    private IPluginLoaderVisitor<String> pluginLoaderVisitor;
    private final IPluginCreator pluginCreator;
    private final IConfigurationManager configurationManager;

    private final static String CONFIG_FILE_NAME = "config.json";
    private final static String LIBRARY_EXTENSION = "jar";
    private final static String PLUGIN_LOADER_KEY = "plugin loader";
    private final static String CONFIGURATION_OBJECT_KEY = "configuration object";
    private final static String END_OF_INPUT_DELIMITER = "\\Z";
    private final static String FEATURE_NAME_DELIMITER = ":";

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on resolution IOC dependencies
     */
    public LoadFeatureActor()
            throws ResolutionException {
        this.pluginLoaderVisitor =          IOC.resolve(Keys.getKeyByName("plugin loader visitor"));
        this.pluginCreator =                IOC.resolve(Keys.getKeyByName("plugin creator"));
        configurationManager =              IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));
    }

    /**
     * Loads feature classes, executes feature plugins and executes feature config
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws LoadFeatureException if any errors occurred on loading feature
     */
    public void load(final LoadFeatureWrapper wrapper)
            throws LoadFeatureException {
        IFeature feature;
        try {
            feature = wrapper.getFeature();
        } catch (ReadValueException e) {
            throw new LoadFeatureException("Feature should not be null.");
        }
        if (feature.isFailed() || null == ModuleManager.getModuleById(feature.getId())) {
            System.out.println("[INFO] --------- Feature '" + feature.getDisplayName() + "' loading is skipped.");
            return;
        }
        IModule currentModule = ModuleManager.getCurrentModule();
        try {
            System.out.println("[INFO] Start loading feature '" + feature.getDisplayName() + "'.");

            File file = Paths.get((feature.getLocation()).getPath()).toFile();
            Collection<IPath> jars = new ArrayList<>();
            Stream.of(
                    file.listFiles((item, string) ->  string.endsWith(LIBRARY_EXTENSION))
            ).map(Path::new).forEach(jars::add);

            IBootstrap<String> bootstrap = IOC.resolve(Keys.getKeyByName("bootstrap"));

            IAction<Class> classHandler = clz -> {
                try {
                    if (Modifier.isAbstract(clz.getModifiers())) {
                        // Ignore abstract classes.
                        return;
                    }

                    IPlugin plugin = pluginCreator.create(clz, bootstrap);
                    plugin.load();
                } catch (PluginCreationException | PluginException e) {
                    throw new ActionExecutionException(e);
                }
            };
            // setup current feature for class loading, bootstrap and applying config
            ModuleManager.setCurrentModule(ModuleManager.getModuleById(feature.getId()));
            IPluginLoader<Collection<IPath>> pluginLoader = IOC.resolve(
                    Keys.getKeyByName(PLUGIN_LOADER_KEY),
                    ModuleManager.getCurrentClassLoader(),
                    classHandler,
                    pluginLoaderVisitor);
            pluginLoader.loadPlugins(jars);

            try {

                AfterFeaturesCallbackStorage.setLocalCallbackQueue(wrapper.getAfterFeaturesCallbackQueue());

                bootstrap.start();
            } catch (ProcessExecutionException e) {
                try {
                    bootstrap.revert();
                } catch (RevertProcessExecutionException ee) {
                    e.addSuppressed(ee);
                }

                throw e;
            }

            File[] files = file.listFiles((item, string) ->  string.equals(CONFIG_FILE_NAME));
            if (null != files && files.length > 0) {
                File configFile = files[0];
                String configString = new Scanner(configFile).useDelimiter(END_OF_INPUT_DELIMITER).next();
                configurationManager.applyConfig(IOC.resolve(Keys.getKeyByName(CONFIGURATION_OBJECT_KEY), configString));
            }
            System.out.println("[OK] -------------- Feature '" + feature.getDisplayName() + "' loaded successfully.");
        } catch (Throwable e) {
            feature.setFailed(true);
            System.out.println("[FAILED] ---------- Feature '" + feature.getDisplayName() + "' loading failed with exception:");
            e.printStackTrace(System.out);
        }
        ModuleManager.setCurrentModule(currentModule);
    }
}

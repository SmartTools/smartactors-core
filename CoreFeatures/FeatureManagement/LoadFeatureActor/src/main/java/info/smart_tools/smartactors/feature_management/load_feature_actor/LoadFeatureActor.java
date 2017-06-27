package info.smart_tools.smartactors.feature_management.load_feature_actor;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
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
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

import java.io.File;
import java.lang.reflect.Modifier;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;

/**
 * Actor that loads feature classes to the classloader,
 * runs feature plugins and executes feature config
 */
public class LoadFeatureActor {

    private static final String CONFIG_FILE = "config.json";

    private IPluginLoaderVisitor<String> pluginLoaderVisitor;
    private final IPluginCreator pluginCreator;
    private final IConfigurationManager configurationManager;

    private final IFieldName featureNameFN;
    private final IFieldName afterFeaturesCallbackQueueFN;

    /**
     * Default constructor
     * @throws ResolutionException if any errors occurred on resolution IOC dependencies
     */
    public LoadFeatureActor()
            throws ResolutionException {
        this.pluginLoaderVisitor = IOC.resolve(Keys.getOrAdd("plugin loader visitor"));
        this.pluginCreator = IOC.resolve(Keys.getOrAdd("plugin creator"));
        configurationManager = IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));
        this.featureNameFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "featureName");
        this.afterFeaturesCallbackQueueFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "afterFeaturesCallbackQueue");
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
        try {
            System.out.println("[INFO] Start loading feature - '" + feature.getName() + "'.");
            String pattern = ".jar";
            File file = Paths.get(((IPath) feature.getFeatureLocation()).getPath()).toFile();
            File configJson = Paths.get(file.getPath(), CONFIG_FILE).toFile();
            this.updateFeature(configJson, feature);
            Collection<IPath> jars = new ArrayList<>();
            Stream.of(file.listFiles((item, string) ->  string.endsWith(pattern))).map(Path::new).forEach(jars::add);

            IBootstrap<IBootstrapItem<String>> bootstrap = new Bootstrap();
            IAction<Class> classHandler = clz -> {
                try {
                    if (Modifier.isAbstract(clz.getModifiers())) {
                        // Ignore abstract classes.
                        return;
                    }

                    IPlugin plugin = pluginCreator.create(clz, bootstrap);
                    plugin.load();
                } catch (PluginCreationException | PluginException e) {
                    throw new ActionExecuteException(e);
                }
            };

            IPluginLoader<Collection<IPath>> pluginLoader = IOC.resolve(
                    Keys.getOrAdd("plugin loader"),
                    classHandler,
                    pluginLoaderVisitor);
            pluginLoader.loadPlugin(jars);

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

            File[] files = file.listFiles((item, string) ->  string.equals("config.json"));
            if (null != files && files.length > 0) {
                File configFile = files[0];
                String configString = new Scanner(configFile).useDelimiter("\\Z").next();
                configurationManager.applyConfig(
                        IOC.resolve(Keys.getOrAdd("configuration object"), configString)
                );
            }
            System.out.println("[OK] -------------- Feature - '" + feature.getName() + "' has been loaded successful.");
        } catch (Throwable e) {
            feature.setFailed(true);
            System.out.println("[FAILED] ---------- Feature '" + feature.getName() + "' loading has been broken with exception:");
            System.out.println(e.getMessage());
        }
    }

    private void updateFeature(final File f, final IFeature feature)
            throws Exception {
        if (f.exists()) {
            String content = new Scanner(f).useDelimiter("\\Z").next();
            IObject config = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()), content);
            String featureName = (String) config.getValue(this.featureNameFN);
            feature.setName(featureName);
        }
    }
}

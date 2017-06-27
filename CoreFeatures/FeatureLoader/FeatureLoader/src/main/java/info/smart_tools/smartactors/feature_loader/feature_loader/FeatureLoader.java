package info.smart_tools.smartactors.feature_loader.feature_loader;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.IBiAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.IFeatureLoader;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.IFeatureStatus;
import info.smart_tools.smartactors.feature_loader.interfaces.ifeature_loader.exceptions.FeatureLoadException;
import info.smart_tools.smartactors.feature_loader.interfaces.ifilesystem_facade.IFilesystemFacade;
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
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.exception.PluginLoaderException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 * Implementation written in assumption that there may be at most one feature load request in progress.
 */
public class FeatureLoader implements IFeatureLoader {
    private final ConcurrentHashMap<String, FeatureStatusImpl> statuses = new ConcurrentHashMap<>();

    private final IFieldName featureNameFieldName;
    private final IFieldName afterFeaturesFieldName;
//    TODO:: check for using getClass().getClassLoader() instead of this more detailed
//    private final ExpansibleURLClassLoader classLoader = new ExpansibleURLClassLoader(new URL[0], getClass().getClassLoader());
    private final IPluginCreator pluginCreator;
    private final IPluginLoaderVisitor<String> pluginLoaderVisitor;
    private final IConfigurationManager configurationManager;
    private final IFilesystemFacade fs;

    private final Set<IBootstrapItem<String>> doneItems = ConcurrentHashMap.newKeySet();

    private final AtomicReference<FeatureStatusImpl> currentLoadingStatus = new AtomicReference<>(null);
    private IQueue<ITask> featureCompletionTaskQueue;

    /**
     * The constructor.
     *
     * @throws ResolutionException if fails to resolve any dependencies
     */
    public FeatureLoader()
            throws ResolutionException {
        featureNameFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "featureName");
        afterFeaturesFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "afterFeatures");
        pluginCreator = IOC.resolve(Keys.getOrAdd("plugin creator"));
        pluginLoaderVisitor = IOC.resolve(Keys.getOrAdd("plugin loader visitor"));
        configurationManager = IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));
        fs = IOC.resolve(Keys.getOrAdd("filesystem facade"));
        featureCompletionTaskQueue = IOC.resolve(Keys.getOrAdd("feature group load completion task queue"));
    }

    @Override
    public IFeatureStatus loadGroup(final IPath groupPath)
            throws FeatureLoadException {
        try {
            FeatureStatusImpl metaFeatureStatus = getFeatureStatus0("group@" + groupPath.getPath());

            if (metaFeatureStatus.isInitialized()) {
                return metaFeatureStatus;
            }

            Set<FeatureStatusImpl> statuses = new HashSet<>();

            for (IPath path : fs.listSubdirectories(groupPath)) {
                FeatureStatusImpl status = loadFeature0(path);

                statuses.add(status);
                statuses.addAll(status.getDependencies());
            }

            for (FeatureStatusImpl dependencyStatus : statuses) {
                if (!dependencyStatus.isInitialized()) {
                    throw new FeatureLoadException(
                            MessageFormat.format("Feature named {0} not found.", dependencyStatus.getId()));
                }

                metaFeatureStatus.addDependency(dependencyStatus);
            }

            metaFeatureStatus.init(groupPath, IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));

            preStartProcess(metaFeatureStatus);

            for (FeatureStatusImpl status : statuses) {
                status.load();
            }

            metaFeatureStatus.load();

            return metaFeatureStatus;
        } catch (IOException | FeatureLoadException | ActionExecuteException | ResolutionException e) {
            throw new FeatureLoadException(
                    MessageFormat.format("Error occurred loading features from ''{0}''.", groupPath.getPath()), e);
        }
    }

    @Override
    public IFeatureStatus loadFeature(final IPath featurePath)
            throws FeatureLoadException {
        FeatureStatusImpl status = loadFeature0(featurePath);

        for (FeatureStatusImpl dependencyStatus : status.getDependencies()) {
            if (!dependencyStatus.isLoaded()) {
                throw new FeatureLoadException(
                        MessageFormat.format("Feature ''{0}'' required by ''{1}'' is not loaded.",
                                dependencyStatus.getId(), status.getId()));
            }
        }

        try {
            preStartProcess(status);
            status.load();
        } catch (ActionExecuteException e) {
            throw new FeatureLoadException("Error occurred loading the feature.", e);
        }

        return status;
    }

    @Override
    public IFeatureStatus getFeatureStatus(final String featureId) {
        return getFeatureStatus0(featureId);
    }

    private FeatureStatusImpl loadFeature0(final IPath featurePath)
            throws FeatureLoadException {
        try {
            IObject featureConfig = readFeatureConfig(featurePath);

            String featureName = (String) featureConfig.getValue(featureNameFieldName);
            List<String> featureDependencies = (List<String>) featureConfig.getValue(afterFeaturesFieldName);

            FeatureStatusImpl status = getFeatureStatus0(featureName);

            if (status.isInitialized()) {
                throw new FeatureLoadException(
                        MessageFormat.format("There already is a feature named ''{0}'' (in directory {1}).",
                                featureName, status.getPath().getPath()));
            }

            status.init(featurePath, featureConfig);

            for (String dependencyId : featureDependencies) {
                status.addDependency(getFeatureStatus0(dependencyId));
            }

            return status;
        } catch (Exception e) {
            throw new FeatureLoadException(
                    MessageFormat.format("Error occurred loading a feature from ''{0}''.", featurePath.getPath()), e);
        }
    }

    private FeatureStatusImpl getFeatureStatus0(final String featureId) {
        return statuses.computeIfAbsent(featureId, id -> {
            try {
                return IOC.resolve(
                        Keys.getOrAdd(FeatureStatusImpl.class.getCanonicalName()),
                        id,
                        (IBiAction<IObject, IPath>) this::loadPluginsAndConfig);
            } catch (ResolutionException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private Collection<IPath> listJarsIn(final IPath dirPath)
            throws FeatureLoadException {
        try {
            return fs.listFiles(dirPath, path -> path.getPath().endsWith(".jar"));
        } catch (IOException e) {
            throw new FeatureLoadException(MessageFormat.format("Could not list jar files in {0}", dirPath.getPath()), e);
        }
    }

    private IObject readFeatureConfig(final IPath featurePath)
            throws FeatureLoadException {
        try {
            String configString = fs.readToString(fs.joinPaths(featurePath, new Path("config.json")));
            return IOC.resolve(Keys.getOrAdd("configuration object"), configString);
        } catch (IOException | ResolutionException | InvalidArgumentException e) {
            throw new FeatureLoadException("Error occurred reading feature configuration file.", e);
        }
    }

    private void loadPluginsAndConfig(final IObject config, final IPath directory)
            throws ActionExecuteException {
        try {
            loadPluginsFrom(listJarsIn(directory));
            configurationManager.applyConfig(config);
        } catch (FeatureLoadException | InvalidArgumentException | PluginLoaderException | ProcessExecutionException |
                 ConfigurationProcessingException | ResolutionException e) {
            throw new ActionExecuteException(e);
        }
    }

    private void loadPluginsFrom(final Collection<IPath> jars)
            throws InvalidArgumentException, PluginLoaderException, ProcessExecutionException, ResolutionException {
        IBootstrap<IBootstrapItem<String>> bootstrap = new Bootstrap(doneItems);
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
                getClass().getClassLoader(),
                classHandler,
                pluginLoaderVisitor);
        pluginLoader.loadPlugin(jars);

        try {
            doneItems.addAll(bootstrap.start());
        } catch (ProcessExecutionException e) {
            try {
                bootstrap.revert();
            } catch (RevertProcessExecutionException ee) {
                e.addSuppressed(ee);
            }

            throw e;
        }
    }

    private void preStartProcess(final FeatureStatusImpl status) throws FeatureLoadException {
        if (!currentLoadingStatus.compareAndSet(null, status)) {
            FeatureStatusImpl curStat = currentLoadingStatus.get();
            throw new FeatureLoadException(String.format("There is some feature load process in progress (feature '%s').",
                    (null == curStat) ? "<null>" : curStat.getId()));
        }

        try {
            status.whenDone(err -> {
                try {
                    ITask task;
                    while (null != (task = featureCompletionTaskQueue.tryTake())) {
                        if (null == err) {
                            try {
                                task.execute();
                            } catch (TaskExecutionException e) {
                                throw new ActionExecuteException(e);
                            }
                        }
                    }
                } finally {
                    currentLoadingStatus.compareAndSet(status, null);
                }
            });
        } catch (ActionExecuteException e) {
            throw new FeatureLoadException("Unexpected feature state.");
        }
    }
}

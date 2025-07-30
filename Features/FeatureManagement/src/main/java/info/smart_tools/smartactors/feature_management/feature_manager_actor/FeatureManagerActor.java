package info.smart_tools.smartactors.feature_management.feature_manager_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.class_management.module_manager.exception.ModuleManagerException;
import info.smart_tools.smartactors.feature_management.feature.Feature;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.exception.FeatureManagementException;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.AddFeatureWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.FeatureManagerStateWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.OnFeatureLoadedWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.OnFeatureStepCompletedWrapper;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Actor that manages process of features downloading, unpacking and loading.
 */
public class FeatureManagerActor {

    private static final int DEFAULT_STACK_DEPTH = 5;

    private static final DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_TIME;

    private Map<IMessageProcessor, Set<IFeature>> requestProcesses;
    private Map<IMessageProcessor, Set<IFeature>> requestProcessesForInfo;
    private Map<IMessageProcessor, IFeature> featuresPaused;

    private Map<Object, IFeature> loadedFeatures;
    private Map<Object, IFeature> failedFeatures;
    private Map<Object, IFeature> featuresInProgress;
    private Map<String, Set<String>> featurePluginsCache;

    private final IFieldName featureFN;
    private final IFieldName pluginsFN;
    private final IFieldName afterFeaturesCallbackQueueFN;
    private final IFieldName startTimeOfLoadingFeatureGroupFN;

    private final static String TASK_QUEUE_IOC_NAME = "task_queue";
    private final static String IOBJECT_FACTORY_STRATEGY_NAME = "info.smart_tools.smartactors.iobject.iobject.IObject";
    private final static String FIELD_NAME_FACTORY_STARTEGY_NAME =
            "info.smart_tools.smartactors.iobject.ifield_name.IFieldName";
    private final static String MESSAGE_PROCESSOR_SEQUENCE_FACTORY_STRATEGY_NAME =
            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence";
    private final static String MESSAGE_PROCESSOR_FACTORY_STRATEGY_NAME =
            "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor";
    private final static String FEATURE_NAME_DELIMITER = ":";

    /**
     * Default constructor
     *
     * @throws ResolutionException if any errors occurred on resolution IOC dependencies
     */
    public FeatureManagerActor()
            throws ResolutionException {
        this.requestProcesses = new ConcurrentHashMap<>();
        this.requestProcessesForInfo = new ConcurrentHashMap<>();
        this.featuresPaused = new ConcurrentHashMap<>();

        this.loadedFeatures = new ConcurrentHashMap<>();
        this.failedFeatures = new ConcurrentHashMap<>();
        this.featuresInProgress = new ConcurrentHashMap<>();
        this.featurePluginsCache = new ConcurrentHashMap<>();

        this.featureFN = IOC.resolve(
                Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "feature"
        );
        this.afterFeaturesCallbackQueueFN = IOC.resolve(
                Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "afterFeaturesCallbackQueue"
        );
        this.startTimeOfLoadingFeatureGroupFN = IOC.resolve(
                Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "startTimeOfLoadingFeatureGroup"
        );
        this.pluginsFN = IOC.resolve(
            Keys.getKeyByName(FIELD_NAME_FACTORY_STARTEGY_NAME), "plugins"
        );

        Map<Object, IModule> modules = ModuleManager.getModules();
        loadCore(modules);
    }

    private void loadCore(
            final Map<Object, IModule> modules
    ) {
        modules.forEach((id, module) -> {
            try {
                Set<String> plugins = readPlugins(module);
                Set<String> dependencies = module.getDependencies()
                    .stream()
                    .map(IModule::getName)
                    .collect(Collectors.toSet());
                String[] featureName = parseFullName(module.getName());
                Feature feature = new Feature(
                        id,
                        featureName[0],
                        featureName[1],
                        featureName[2],
                        dependencies,
                        plugins,
                        null,
                        null,
                        null
                );

                loadedFeatures.put(id, feature);
                if (!plugins.isEmpty()) {
                    featurePluginsCache.put(feature.getDisplayName(), plugins);
                }
            } catch (FeatureManagementException e) {
                // TODO: add better handling for an exception
                throw new RuntimeException("Failed to add feature");
            }
        });
    }

    private Set<String> readPlugins(final IModule module) {
        Optional<URL> optionalURL = Arrays.stream(module.getClassLoader().getURLsFromDependencies())
            .filter(url -> url.getFile().contains(module.getName().split(":")[1]))
            .findFirst()
            .map(url -> {
                try {
                    return new URL("jar:" + url.getFile() + "config.json");
                } catch (MalformedURLException e) {
                    return null;
                }
            });

        if (!optionalURL.isPresent()) {
            return new HashSet<>();
        }

        List<String> plugins;
        try (InputStream is = optionalURL.get().openStream()) {
            IObject config = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME), is);
            plugins = (List<String>) config.getValue(this.pluginsFN);
        } catch (IOException | ResolutionException e) {
            throw new RuntimeException("Unable to read " + optionalURL.get().getFile(), e);
        } catch (InvalidArgumentException | ReadValueException e) {
            throw new RuntimeException("Unable to read 'plugins' section from config", e);
        }

        return plugins == null ? new HashSet<>() : new HashSet<>(plugins);
    }

    /**
     * Adds features to the processing
     *
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureManagementException if any errors occurred on feature processing
     */
    public void addFeatures(final AddFeatureWrapper wrapper)
            throws FeatureManagementException {
        try {
            LocalTime startLoadingTime = LocalTime.now();
            Set<IFeature> features = new HashSet<>(wrapper.getFeatures());
            Set<IFeature> featuresForInfo = new HashSet<>(wrapper.getFeatures());
            IMessageProcessor mp = wrapper.getMessageProcessor();
            mp.getContext().setValue(this.startTimeOfLoadingFeatureGroupFN, startLoadingTime);

            int stackDepth = DEFAULT_STACK_DEPTH;
            String scatterChainName = wrapper.getScatterChainName();
            IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName(TASK_QUEUE_IOC_NAME));

            IQueue afterFeaturesCallbackQueue = IOC.resolve(Keys.getKeyByName(IQueue.class.getCanonicalName()));

            int count = 0;
            for (IFeature feature : features) {
                this.featuresInProgress.put(feature.getId(), feature);
                IMessageProcessingSequence processingSequence = IOC.resolve(
                        Keys.getKeyByName(MESSAGE_PROCESSOR_SEQUENCE_FACTORY_STRATEGY_NAME),
                        stackDepth,
                        scatterChainName,
                        mp.getMessage()
                );
                IMessageProcessor messageProcessor = IOC.resolve(
                        Keys.getKeyByName(MESSAGE_PROCESSOR_FACTORY_STRATEGY_NAME), queue, processingSequence
                );
                IObject message = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME));
                message.setValue(this.featureFN, feature.clone());
                message.setValue(this.afterFeaturesCallbackQueueFN, afterFeaturesCallbackQueue);
                IObject context = IOC.resolve(Keys.getKeyByName(IOBJECT_FACTORY_STRATEGY_NAME));
                messageProcessor.process(message, context);
                ++count;
            }
            if (count > 0) {
                mp.pauseProcess();
                this.requestProcesses.put(mp, features);
                this.requestProcessesForInfo.put(mp, featuresForInfo);
            }
        } catch (
                ReadValueException |
                        ChangeValueException |
                        InvalidArgumentException |
                        ResolutionException |
                        MessageProcessorProcessException |
                        AsynchronousOperationException e
        ) {
            throw new FeatureManagementException(e);
        }
    }

    /**
     * Ends feature processing
     *
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureManagementException if any errors occurred on feature processing
     */
    public void onFeatureLoaded(final OnFeatureLoadedWrapper wrapper)
            throws FeatureManagementException {
        IFeature feature, featureFromMessage;
        try {
            featureFromMessage = wrapper.getFeature();
            feature = this.featuresInProgress.get(featureFromMessage.getId());
            if (!feature.updateFromClone(featureFromMessage)) {
                throw new FeatureManagementException("Cannot update feature " + feature.getDisplayName() + " from its clone.");
            }
            this.featuresInProgress.remove(feature.getId());
            if (feature.isFailed()) {
                this.failedFeatures.put(feature.getId(), feature);

            } else {
                if (null != ModuleManager.getModuleById(feature.getId())) {
                    this.loadedFeatures.put(feature.getId(), feature);
                }
                for (IFeature processingFeature : this.featuresInProgress.values()) {
                    removeLoadedFeaturesFromFeatureDependencies(processingFeature);
                }
                checkAndRunConnectedFeatures();
            }

            Collection<IMessageProcessor> processesToContinue = new HashSet<>();
            this.requestProcesses.forEach((k, v) -> {
                v.remove(feature);
                if (v.isEmpty()) {
                    processesToContinue.add(k);
                    this.requestProcesses.remove(k);
                }
            });
            processesToContinue.forEach((mp) -> {
                try {

                    IQueue<ITask> afterFeatureCallbackQueue = wrapper.getAfterFeaturesCallbackQueue();
                    ITask task;
                    while (null != (task = afterFeatureCallbackQueue.tryTake())) {
                        try {
                            task.execute();
                        } catch (TaskExecutionException e) {
                            throw new ActionExecutionException(e);
                        }
                    }

                    LocalTime startLoadingTime = (LocalTime) mp.getContext().getValue(
                            this.startTimeOfLoadingFeatureGroupFN
                    );
                    mp.continueProcess(null);
                    Duration elapsedInterval = Duration.between(startLoadingTime, LocalTime.now());
                    LocalTime elapsedTimeToLocalTime = LocalTime.ofNanoOfDay(elapsedInterval.toNanos());

                    System.out.println("\n\n");
                    System.out.println(
                            "[\033[1;34mINFO\033[0m] Feature group has been loaded: " +
                                    this.requestProcessesForInfo.get(mp).stream().map(
                                            a -> "\n\t" + (!a.isFailed() ? "(\033[0;32mOK\033[0m) " : "(\033[0;30mFailed\033[0m) ") + a.getDisplayName()
                                    ).collect(Collectors.toList())
                    );
                    System.out.println("[\033[1;34mINFO\033[0m] elapsed time - " + elapsedTimeToLocalTime.format(df) + ".");
                    System.out.println("\n\n");
                    this.requestProcessesForInfo.remove(mp);
                } catch (
                        InvalidArgumentException |
                                AsynchronousOperationException |
                                ReadValueException |
                                ActionExecutionException e
                ) {
                    throw new RuntimeException(e);
                }
            });
            checkUnresolved();

        } catch (ReadValueException e) {
            throw new FeatureManagementException("Feature should not be null.");
        }
        if (!featureFromMessage.updateFromClone(feature)) {
            throw new FeatureManagementException(
                    "Cannot update feature " + featureFromMessage.getDisplayName() + " from its clone."
            );
        }
    }

    /**
     * Ends step of feature processing
     *
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureManagementException if any errors occurred on feature processing
     */
    public void onFeatureStepCompleted(final OnFeatureStepCompletedWrapper wrapper)
            throws FeatureManagementException {
        IFeature feature, featureFromMessage;
        try {
            featureFromMessage = wrapper.getFeature();
            feature = this.featuresInProgress.get(featureFromMessage.getId());
            if (!feature.updateFromClone(featureFromMessage)) {
                throw new FeatureManagementException("Cannot update feature " + feature.getDisplayName() + " from its clone.");
            }

            Set<String> featureDependencies = feature.getDependencies();
            Set<String> plugins = feature.getPlugins();

            if (plugins != null && !plugins.isEmpty()) {
                featurePluginsCache.put(feature.getDisplayName(), plugins);
            }

            for (Map.Entry<String, Set<String>> cache : this.featurePluginsCache.entrySet()) {
                if (featureDependencies != null &&
                    featureDependencies.contains(cache.getKey()) &&
                    !cache.getValue().contains(feature.getDisplayName())
                ) {
                    featureDependencies.remove(cache.getKey());
                    featureDependencies.addAll(cache.getValue());
                }
            }

            replaceDependencies(featuresPaused, plugins, feature);
            replaceDependencies(featuresInProgress, plugins, feature);

            checkAndRunConnectedFeatures();

            if (null != featureDependencies) {
                boolean hasDuplicate = false;
                // check if feature with same name/version has already been loaded
                for (IFeature loadedFeature : this.loadedFeatures.values()) {
                    if (loadedFeature.getDisplayName().equals(feature.getDisplayName())) {
                        hasDuplicate = true;
                        break;
                    }
                }
                // or in progress
                for (IFeature featureInProgress : this.featuresInProgress.values()) {
                    if (featureInProgress.getDisplayName().equals(feature.getDisplayName()) &&
                            featureInProgress.getId() != feature.getId() &&
                            null != ModuleManager.getModuleById(featureInProgress.getId()) &&
                            !featureInProgress.isFailed()) {
                        hasDuplicate = true;
                        break;
                    }
                }

                if (!hasDuplicate) {
                    ModuleManager.addModule(
                            feature.getId(),
                            emptify(feature.getGroupId()) + FEATURE_NAME_DELIMITER + emptify(feature.getName()),
                            emptify(feature.getVersion())
                    );

                    removeLoadedFeaturesFromFeatureDependencies(feature);

                    if (featureDependencies.isEmpty()) {
                        ModuleManager.finalizeModuleDependencies(feature.getId());
                    } else {
                        IMessageProcessor mp = wrapper.getMessageProcessor();
                        mp.pauseProcess();
                        this.featuresPaused.put(mp, feature);
                        if (this.featuresPaused.size() == this.featuresInProgress.size()) {
                            checkUnresolved();
                        }
                    }
                }
            }
        } catch (InvalidArgumentException | ModuleManagerException | ReadValueException | AsynchronousOperationException e) {
            throw new FeatureManagementException(e);
        }
        if (!featureFromMessage.updateFromClone(feature)) {
            throw new FeatureManagementException(
                    "Cannot update feature " + featureFromMessage.getDisplayName() + " from its clone."
            );
        }
    }

    /**
     * Gets state of added features
     *
     * @param wrapper the wrapped message for getting needed data and storing result
     * @throws FeatureManagementException if any errors occurred on feature processing
     */
    public void getState(final FeatureManagerStateWrapper wrapper)
            throws FeatureManagementException {
        try {
            wrapper.setLoadedFeatures(this.loadedFeatures.values());
            wrapper.setFailedFeatures(this.failedFeatures.values());
            wrapper.setProcessingFeatures(this.featuresInProgress.values());
            wrapper.setFrozenFeatureProcesses(this.featuresPaused);
            wrapper.setFrozenRequests(this.requestProcesses);
        } catch (ChangeValueException e) {
            throw new FeatureManagementException("Could not set parameter to IObject.", e);
        }
    }

    private void checkAndRunConnectedFeatures() {
        Collection<IMessageProcessor> needContinueFeatures = new HashSet<>();
        this.featuresPaused.forEach((k, v) -> {
            if (v.getDependencies().isEmpty()) {
                needContinueFeatures.add(k);
                this.featuresPaused.remove(k);
            }
        });
        needContinueFeatures.forEach((mp) -> {
            try {
                mp.continueProcess(null);
            } catch (AsynchronousOperationException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private void removeLoadedFeaturesFromFeatureDependencies(final IFeature feature)
            throws FeatureManagementException {

        Set<String> featureDependencies = feature.getDependencies();
        // check if feature has dependencies and not skipped
        if (null != featureDependencies && null != ModuleManager.getModuleById(feature.getId())) {
            for (Iterator<String> iterator = featureDependencies.iterator(); iterator.hasNext(); ) {

                String dependency = iterator.next();

                IFeature baseFeature = null;
                int order = -1;
                for (IFeature loadedFeature : this.loadedFeatures.values()) {
                    order = compareFeatureToDependency(loadedFeature, dependency);
                    if (order == 0) {
                        baseFeature = loadedFeature;
                        break;
                    } else if (order > 0) {
                        if (baseFeature == null ||
                                emptify(loadedFeature.getVersion()).
                                        compareTo(emptify(baseFeature.getVersion())) >= 0) {
                            baseFeature = loadedFeature;
                        }
                    }
                }
                if (null != baseFeature) {
                    try {
                        ModuleManager.addModuleDependency(feature.getId(), baseFeature.getId());
                    } catch (InvalidArgumentException e) {
                        throw new FeatureManagementException("Cannot add dependency to module.", e);
                    }
                    if (order == 1) {
                        System.out.println(
                                "[\033[1;33mWARNING\033[0m] Version of base feature '" + baseFeature.getDisplayName() +
                                        "' is greater than in feature '" + feature.getDisplayName() + "' dependencies."
                        );
                    }
                    iterator.remove();
                }
            }
        }
    }

    private void checkUnresolved()
            throws FeatureManagementException {
        int minDependencies = this.featuresInProgress
                .values()
                .stream()
                .filter(feature -> null != feature.getDependencies())
                .map(f -> f.getDependencies().size())
                .min(Integer::compareTo)
                .orElse(0);
        if (
                !this.featuresInProgress.isEmpty()
                        && minDependencies > 0
        ) {
            Set<String> allUnresolved = new HashSet<>();
            for (IFeature feature : this.featuresInProgress.values()) {
                if (feature.getDependencies() != null) {
                    allUnresolved.addAll(feature.getDependencies());
                }
            }

            Set<String> unresolved = new HashSet<>(allUnresolved);
            for (String dependency : allUnresolved) {
                for (IFeature feature : this.featuresInProgress.values()) {
                    if (compareFeatureToDependency(feature, dependency) >= 0) {
                        unresolved.remove(dependency);
                    }
                }
            }
            /*Iterator<String> iterator = unresolved.iterator();
            while (iterator.hasNext()) {
                String dependency = iterator.next();

                for (IFeature feature : this.featuresInProgress.values()) {
                    if (compareFeatureToDependency(feature, dependency) >= 0) {
                        iterator.remove();
                    }
                }
            }*/

            System.out.println("[\033[1;34mINFO\033[0m] The server waits for the following features to continue: " +
                    unresolved.stream().map(a -> "\n\t\t" + a).collect(Collectors.toList())
            );
        }
    }

    /**
     * Replace feature dependencies with plugins
     * @param features map of features where replacement needs to be performed
     * @param plugins set of plugins from current feature
     * @param feature current feature
     */
    private void replaceDependencies(
        final Map<?, IFeature> features,
        final Set<String> plugins,
        final IFeature feature
    ) {
        for (Map.Entry<?, IFeature> entry : features.entrySet()) {
            // if there are no plugins, then there is no need to update features
            if (plugins == null) {
                continue;
            }

            // if plugins cache contains feature from entry set, then skip cycle
            // otherwise, it may create cycled dependency
            if (this.featurePluginsCache.containsKey(entry.getValue().getDisplayName())) {
                continue;
            }
            // if feature from entry set contains current feature as a plugin, then skip cycle
            if (entry.getValue().getPlugins() != null && entry.getValue().getPlugins().contains(feature.getDisplayName())) {
                continue;
            }
            // if feature plugins contain feature from entry set as a plugin, then skip cycle
            if (plugins.contains(entry.getValue().getDisplayName())) {
                continue;
            }

            Set<String> dependencies = entry.getValue().getDependencies();
            if (dependencies != null && dependencies.contains(feature.getDisplayName()) && !plugins.isEmpty()
            ) {
                dependencies.remove(feature.getDisplayName());
                dependencies.addAll(plugins);
            }
        }
    }

    private String emptify(String str) {
        return (str == null ? "" : str);
    }

    private int compareFeatureToDependency(IFeature feature, String dependencyName)
            throws FeatureManagementException {
        String[] dependency = parseFullName(dependencyName);
        if (!emptify(feature.getGroupId()).equals(dependency[0]) ||
                !emptify(feature.getName()).equals(dependency[1])) {
            return -1;
        }
        if (dependency[2].equals("")) {
            return 2;
        }
        return emptify(feature.getVersion()).compareTo(dependency[2]);
    }

    // todo: replace this code by parsing strategy
    private String[] parseFullName(String fullName)
            throws FeatureManagementException {
        String[] dependencyNames = fullName.split(FEATURE_NAME_DELIMITER);
        if (dependencyNames.length < 2) {
            throw new FeatureManagementException("Wrong feature name or dependency format '" + fullName + "'.");
        }
        String[] result = {
                dependencyNames[0],
                dependencyNames[1],
                dependencyNames.length > 2 ? dependencyNames[2] : ""
        };
        return result;
    }
}

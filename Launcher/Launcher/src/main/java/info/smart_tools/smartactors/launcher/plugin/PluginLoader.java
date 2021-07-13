package info.smart_tools.smartactors.launcher.plugin;

import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.classloader.SmartactorsClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.iaction.ActionExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderInitException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import info.smart_tools.smartactors.launcher.logger.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginLoader implements IPluginLoader<List<IFeature>> {

    private static final ILogger log = LoggerFactory.getLogger();

    private static final String FEATURE_NAME_DELIMITER = ":";
    private static final String CLASS_EXTENSION = ".class";

    /**
     * ClassLoader for load classes
     */
    private final IClassLoaderWrapper classLoader;
    private final IDependencyLoader dependencyLoader;
    private final IBootstrap bootstrap;
    private final IPluginCreator pluginCreator;

    public PluginLoader(
            final IClassLoaderWrapper classLoader,
            final IDependencyLoader dependencyLoader,
            final IBootstrap bootstrap,
            final IPluginCreator pluginCreator
    ) throws PluginLoaderInitException {
        if (null == classLoader) {
            throw new PluginLoaderInitException("Incoming argument should not be null.");
        }

        this.classLoader = classLoader;
        this.dependencyLoader = dependencyLoader;
        this.bootstrap = bootstrap;
        this.pluginCreator = pluginCreator;
    }

    @Override
    public void loadPlugins(
            final List<IFeature> sortedFeatures,
            final IObjectMapper objectMapper
    ) throws PluginLoaderException {
        IModule currentModule = ModuleManager.getCurrentModule();

        for (IFeature feature : sortedFeatures) {
            String pathToJar = feature.getFileName();

            try (JarFile jarFile = new JarFile(pathToJar)) {
                pathToJar = feature.getFileName();
                Enumeration<JarEntry> iterator = jarFile.entries();

                Object featureId = feature.getId();
                String[] featureName = parseFullName(feature.getName());
                String featureVersion = featureName[2];
                ModuleManager.addModule(featureId, feature.getName(), featureVersion);

                if (!feature.getAfterFeatures().isEmpty()) {
                    List<IFeature> dependencies = sortedFeatures.stream()
                            .filter(it -> feature.getAfterFeatures().contains(it.getName()))
                            .collect(Collectors.toList());
//                    log.debug("Loading dependencies for feature \"{0}\"", feature.getName());
                    for (IFeature dependency : dependencies) {
//                        log.debug("Adding dependency ID \"{0}\", name \"{1}\"", dependency.getId(), dependency.getName());
                        ModuleManager.addModuleDependency(feature.getId(), dependency.getId());
                    }
                    ModuleManager.finalizeModuleDependencies(featureId);
                }

                log.debug(
                        "'{'\"featureState\": \"LOADING_INITIALIZED\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                        feature.getName(),
                        Instant.now().toString()
                );

                ModuleManager.setCurrentModule(ModuleManager.getModuleById(featureId));
                IClassLoaderWrapper moduleClassLoader = SmartactorsClassLoaderWrapper.newInstance(ModuleManager.getCurrentClassLoader());
                Class<?> pluginClass = moduleClassLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin");

                log.debug(
                        "'{'\"featureState\": \"READING_JARS\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                        feature.getName(),
                        Instant.now().toString()
                );
                dependencyLoader.load(
                        moduleClassLoader,
                        sortedFeatures.stream()
                                .filter(it -> it.getName().equals(feature.getName()))
                                .map(IFeature::getPath)
                                .collect(Collectors.toList())
                );
                log.debug(
                        "'{'\"featureState\": \"JARS_LOADED\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                        feature.getName(),
                        Instant.now().toString()
                );

                while (iterator.hasMoreElements()) {
                    log.debug(
                            "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"DETECTING\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                            feature.getName(),
                            Instant.now().toString()
                    );
                    JarEntry je = iterator.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(CLASS_EXTENSION)) {
                        log.debug(
                                "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"NOT_CLASS_FOUND\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                                je.getName(),
                                feature.getName(),
                                Instant.now().toString()
                        );
                        continue;
                    }

                    String className = je.getName().substring(0, je.getName().length() - CLASS_EXTENSION.length());
                    className = className.replace('/', '.');

                    Class<?> clazz;
                    try {
//                        log.debug("Loading class \"{0}\" for feature \"{1}\"", className, feature.getName());
                        log.debug(
                                "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"LOADING\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                                className,
                                feature.getName(),
                                Instant.now().toString()
                        );
                        clazz = moduleClassLoader.loadClass(className);
                    } catch (Throwable e) {
                        // ignoring, because the plugin which class cannot be loaded cannot be loaded
                        log.debug(
                                "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"LOADING_FAILED\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                                className,
                                feature.getName(),
                                Instant.now().toString()
                        );
//                        System.out.println("[WARNING] Class " + className + " loading failed.");
                        continue;
                    }

                    if (pluginClass.isAssignableFrom(clazz) && clazz != pluginClass) {
                        log.debug(
                                "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"PLUGIN_DETECTED\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                                className,
                                feature.getName(),
                                Instant.now().toString()
                        );
//                        log.debug("Class \"{0}\" is plugin, initializing plugin load", className);
                        loadPlugin(clazz);
                        log.debug(
                                "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"PLUGIN_LOADED\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                                className,
                                feature.getName(),
                                Instant.now().toString()
                        );
                    }
                    log.debug(
                            "'{'\"featureState\": \"LOADING_CLASSES\", \"classState\": \"LOADING_COMPLETE\", \"className\": \"{0}\", \"featureName\": \"{1}\", \"timestamp\": \"{2}\"'}'",
                            className,
                            feature.getName(),
                            Instant.now().toString()
                    );
//                    log.debug("Class \"{0}\" was loaded for feature \"{1}\"", className, feature.getName());
                }

                log.debug(
                        "'{'\"featureState\": \"LOADING_COMPLETE\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                        feature.getName(),
                        Instant.now().toString()
                );
//                log.debug("Loaded feature \"{0}\"", feature.getName());
            } catch (Throwable e) {
                log.debug(
                        "'{'\"featureState\": \"LOADING_FAILED\", \"featureName\": \"{0}\", \"timestamp\": \"{1}\"'}'",
                        feature.getName(),
                        Instant.now().toString()
                );
                throw new PluginLoaderException("Plugin loading failed: " + pathToJar, e);
            }
        }

        ModuleManager.setCurrentModule(currentModule);
    }

    private String[] parseFullName(String fullName)
            throws RuntimeException {
        String[] dependencyNames = fullName.split(FEATURE_NAME_DELIMITER);
        if (dependencyNames.length < 2) {
            //TODO: replace exception
            throw new RuntimeException("Wrong feature name or dependency format '"+fullName+"'.");
        }
        return new String[]{
                dependencyNames[0],
                dependencyNames[1],
                dependencyNames.length > 2 ? dependencyNames[2] : ""
        };
    }

    private void loadPlugin(
            final Class<?> clz
    ) throws ActionExecutionException {
        try {
            if (Modifier.isAbstract(clz.getModifiers())) {
                return;
            }

            Object plugin = pluginCreator.create(clz, bootstrap);
            Method loadPlugin = plugin.getClass().getMethod("load");
            loadPlugin.invoke(plugin);
        } catch (Exception e) {
            throw new ActionExecutionException(e);
        }
    }
}


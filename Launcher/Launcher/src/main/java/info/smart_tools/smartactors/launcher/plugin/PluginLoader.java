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
import info.smart_tools.smartactors.launcher.logger.Logger;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.Instant;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginLoader implements IPluginLoader<List<IFeature>> {

    private final ILogger log;

    private final IDependencyLoader dependencyLoader;
    private final IBootstrap bootstrap;
    private final IPluginCreator pluginCreator;

    public PluginLoader(
            final IDependencyLoader dependencyLoader,
            final IBootstrap bootstrap,
            final IPluginCreator pluginCreator
    ) throws PluginLoaderInitException {
        if (null == dependencyLoader || null == bootstrap || null == pluginCreator) {
            throw new PluginLoaderInitException("Incoming argument should not be null.");
        }

        this.log = new Logger();

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
                    for (IFeature dependency : dependencies) {
                        ModuleManager.addModuleDependency(feature.getId(), dependency.getId());
                    }
                    ModuleManager.finalizeModuleDependencies(featureId);
                }

                logState(feature.getName(), "LOADING_INITIALIZED", null, null);

                ModuleManager.setCurrentModule(ModuleManager.getModuleById(featureId));
                IClassLoaderWrapper moduleClassLoader = new SmartactorsClassLoaderWrapper(ModuleManager.getCurrentClassLoader());
                Class<?> pluginClass = moduleClassLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin");

                logState(feature.getName(), "READING_JARS", null, null);
                dependencyLoader.load(
                        moduleClassLoader,
                        sortedFeatures.stream()
                                .filter(it -> it.getName().equals(feature.getName()))
                                .map(IFeature::getPath)
                                .collect(Collectors.toList())
                );
                logState(feature.getName(), "JARS_LOADED", null, null);

                while (iterator.hasMoreElements()) {
                    logState(feature.getName(), "LOADING_CLASSES", null, "DETECTING");
                    JarEntry je = iterator.nextElement();
                    String classExtension = ".class";

                    if (je.isDirectory() || !je.getName().endsWith(classExtension)) {
                        logState(feature.getName(), "LOADING_CLASSES", je.getName(), "NOT_CLASS_FOUND");
                        continue;
                    }

                    String className = je.getName().substring(0, je.getName().length() - classExtension.length());
                    className = className.replace('/', '.');

                    Class<?> clazz;
                    try {
                        logState(feature.getName(), "LOADING_CLASSES", className, "LOADING");
                        clazz = moduleClassLoader.loadClass(className);
                    } catch (Throwable e) {
                        // ignoring, because the plugin which class cannot be loaded cannot be loaded
                        logState(feature.getName(), "LOADING_CLASSES", className, "LOADING_FAILED");
                        continue;
                    }

                    if (pluginClass.isAssignableFrom(clazz) && clazz != pluginClass) {
                        logState(feature.getName(), "LOADING_CLASSES", className, "PLUGIN_DETECTED");
                        loadPlugin(clazz);
                        logState(feature.getName(), "LOADING_CLASSES", className, "PLUGIN_LOADED");
                    }
                    logState(feature.getName(), "LOADING_CLASSES", className, "LOADING_COMPLETE");
                }

                logState(feature.getName(), "LOADING_COMPLETE", null, null);
            } catch (Throwable e) {
                throw new PluginLoaderException("Plugin loading failed: " + pathToJar, e);
            }
        }

        ModuleManager.setCurrentModule(currentModule);
    }

    private String[] parseFullName(String fullName)
            throws PluginLoaderException {
        String[] dependencyNames = fullName.split(":");
        if (dependencyNames.length < 2) {
            throw new PluginLoaderException("Wrong feature name or dependency format '" + fullName + "'.");
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

    private void logState(
            final String featureName,
            final String featureState,
            final String className,
            final String classState
    ) {
        log.debug(
                "'{'\"featureName\": \"{0}\", \"featureState\": \"{1}\", \"className\": \"{2}\", \"classState\": \"{3}\", \"timestamp\": \"{4}\"'}'",
                featureName,
                featureState,
                className,
                classState,
                Instant.now().toString()
        );
    }
}


package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.bootstrap.Bootstrap;
import info.smart_tools.smartactors.launcher.classloader.SmartactorsClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.core.ServerStarter;
import info.smart_tools.smartactors.launcher.feature.FeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.core.IServerStarter;
import info.smart_tools.smartactors.launcher.interfaces.core.IFeatureLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.ServerStarterException;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.FeatureLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrap.BootstrapInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ObjectMapperInstanceException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreatorInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.launcher.LauncherInitializeException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import info.smart_tools.smartactors.launcher.objectmapper.ObjectMapper;
import info.smart_tools.smartactors.launcher.plugin.PluginCreator;
import info.smart_tools.smartactors.launcher.plugin.PluginLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class Launcher implements ILauncher {

    private final Map<Object, Object> properties;

    private final ILogger log;
    private final IFeatureLoader featureLoader;

    private IServerStarter serverStarter;
    private IClassLoaderWrapper classLoader;

    public Launcher(
            final Map<Object, Object> properties,
            final ILogger log,
            final IFeatureLoader featureLoader
    ) {
        this.log = log;
        this.properties = properties;
        this.featureLoader = featureLoader;
    }

    public Boolean initialize(
            final Map<String, Object> parameters
    ) throws LauncherInitializeException {
        try {
            Thread.currentThread().setName("BaseThread");
            ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));

            IDependencyReplacer dependencyReplacer = (IDependencyReplacer) parameters.get("dependency replacer");
            IDependencyLoader dependencyLoader = (IDependencyLoader) parameters.get("dependency loader");
            IFeatureSorting featureSorting = (IFeatureSorting) parameters.get("feature sorting");

            ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
            this.classLoader = new SmartactorsClassLoaderWrapper(cl);

            File dependencies = new File((String) properties.get("launcher.dependencies.path"));
            ClassLoader jcl = URLClassLoader.newInstance(new URL[]{
                    dependencies.toURI().toURL()
            });
            IObjectMapper objectMapper = new ObjectMapper(jcl);

            IBootstrap bootstrap = new Bootstrap(this.classLoader);
            IPluginCreator pluginCreator = new PluginCreator(this.classLoader);
            IFeatureReader featureReader = new FeatureReader(log, objectMapper);
            IPluginLoader<List<IFeature>> pluginLoader = new PluginLoader(
                    dependencyLoader,
                    bootstrap,
                    pluginCreator
            );

            this.serverStarter = new ServerStarter(
                    log,
                    bootstrap,
                    featureReader,
                    dependencyReplacer,
                    featureSorting,
                    pluginLoader
            );

            return true;
        } catch (PluginCreatorInitException e) {
            throw new LauncherInitializeException("Failed to initialize plugin creator", e);
        } catch (PluginLoaderInitException e) {
            throw new LauncherInitializeException("Failed to initialize plugin loader", e);
        } catch (BootstrapInitException e) {
            throw new LauncherInitializeException("Failed to initialize bootstrap wrapper", e);
        } catch (MalformedURLException e) {
            throw new LauncherInitializeException("Failed to get URL to dependencies", e);
        } catch (ObjectMapperInstanceException e) {
            throw new LauncherInitializeException("Failed to initialize object mapper instance");
        }
    }

    public Boolean start() throws LauncherExecutionException {
        log.info("Starting to load core");
        LocalTime start = LocalTime.now();
        DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_TIME;
        boolean isInitialized;

        try {
            String corePath = (String) properties.get("core.path");
            List<IPath> featureJars = featureLoader.loadFeatures(corePath);
            isInitialized = serverStarter.initServerFeatures(classLoader, featureJars);
        } catch (FeatureLoaderException e) {
            throw new LauncherExecutionException("Failed to load core features", e);
        } catch (ServerStarterException e) {
            throw new LauncherExecutionException("Failed to initialize core features", e);
        }

        Duration elapsedTime = Duration.between(start, LocalTime.now());
        LocalTime elapsedTimeToLocalTime = LocalTime.ofNanoOfDay(elapsedTime.toNanos());

        if (isInitialized) {
            log.info("Stage 1: server core has been loaded");
        } else {
            log.error("Stage 1: failed to load server core");
        }
        log.info("Stage 1: elapsed time - {0}", elapsedTimeToLocalTime.format(df));
        return isInitialized;
    }
}

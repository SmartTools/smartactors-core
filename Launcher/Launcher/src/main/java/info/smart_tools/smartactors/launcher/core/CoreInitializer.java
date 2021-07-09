package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.bootstrap.Bootstrap;
import info.smart_tools.smartactors.launcher.classloader.SmartactorsClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.feature.FeatureReader;
import info.smart_tools.smartactors.launcher.feature.FeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.ICoreInitializer;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreInitializerException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.BootstrapWrapperInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ObjectMapperInstanceException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreatorInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderInitException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import info.smart_tools.smartactors.launcher.logger.LoggerFactory;
import info.smart_tools.smartactors.launcher.objectmapper.ObjectMapper;
import info.smart_tools.smartactors.launcher.plugin.PluginCreator;
import info.smart_tools.smartactors.launcher.plugin.PluginLoader;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

public class CoreInitializer implements ICoreInitializer  {

    private static final ILogger log = LoggerFactory.getLogger();

    private final String dependenciesPath;

    public CoreInitializer(
            final String dependenciesPath
    ) {
        this.dependenciesPath = dependenciesPath;
    }

    @Override
    public void initializeCoreFeatures(
            final List<IPath> coreFeatures
    ) throws CoreInitializerException {
        try {
            File dependencies = new File(dependenciesPath);

            ClassLoader jcl = URLClassLoader.newInstance(new URL[]{
                    dependencies.toURI().toURL()
            });
            ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
            IObjectMapper objectMapper = ObjectMapper.newInstance(jcl);
            IClassLoaderWrapper classLoader = SmartactorsClassLoaderWrapper.newInstance(cl);
            log.info("Initialized class loader and object mapper");

            loadPlugins(coreFeatures, classLoader, objectMapper);
        } catch (PluginCreatorInitException e) {
            throw new CoreInitializerException("Failed to initialize plugin creator", e);
        } catch (MalformedURLException e) {
            throw new CoreInitializerException("Failed to create URL for dependencies", e);
        } catch (PluginLoaderInitException e) {
            throw new CoreInitializerException("Failed to initialize plugin loader", e);
        } catch (FeatureSortingException e) {
            throw new CoreInitializerException("Failed to sort features by dependencies", e);
        } catch (FeatureReaderException e) {
            throw new CoreInitializerException("Failed to read features from jar files", e);
        } catch (PluginLoaderException e) {
            throw new CoreInitializerException("Failed to load plugins in features", e);
        } catch (BootstrapWrapperInitException e) {
            throw new CoreInitializerException("Failed to initialize bootstrap wrapper", e);
        } catch (ObjectMapperInstanceException e) {
            throw new CoreInitializerException("Failed to create ObjectMapper instance", e);
        }
    }

    private void loadPlugins(
            final List<IPath> jars,
            final IClassLoaderWrapper classLoader,
            final IObjectMapper objectMapper
    ) throws PluginLoaderInitException,
            PluginLoaderException,
            FeatureReaderException,
            FeatureSortingException,
            BootstrapWrapperInitException,
            PluginCreatorInitException
    {
        IDependencyLoader dependencyLoader = new DependencyLoader();
        IBootstrap bootstrap = new Bootstrap(classLoader);
        IPluginCreator pluginCreator = new PluginCreator(classLoader);
        IFeatureReader featureReader = new FeatureReader(objectMapper);
        IDependencyReplacer dependencyReplacer = new DependencyReplacer();
        IFeatureSorting featureSorting = new FeatureSorting();
        IPluginLoader<List<IFeature>> pluginLoader = new PluginLoader(
                classLoader,
                dependencyLoader,
                bootstrap,
                pluginCreator
        );

        List<IFeature> features = featureReader.readFeatures(jars);
        List<IFeature> sortedRawFeatures = featureSorting.sortFeatures(features);
        dependencyReplacer.replaceDependencies(sortedRawFeatures);
        // TODO: temporary
        System.out.println("\n\n");
        sortedRawFeatures.stream()
                .map(it -> MessageFormat.format("{0}/{1}", it.getName(), it.getAfterFeatures()))
                .collect(Collectors.toList())
                .forEach(System.out::println);
        System.out.println("\n\n");
        List<IFeature> sortedFeatures = featureSorting.sortFeatures(sortedRawFeatures);

        pluginLoader.loadPlugins(sortedFeatures, objectMapper);

        try {
            log.info("Starting bootstrap...");
            bootstrap.start();
        } catch (Exception e) {
            try {
                log.error("Failed to start bootstrap, reverting... Error: {0}", e.getCause().getCause().getCause().getLocalizedMessage());
                bootstrap.revert();
            } catch (Exception ee) {
                e.addSuppressed(ee);
            }
        }
    }
}

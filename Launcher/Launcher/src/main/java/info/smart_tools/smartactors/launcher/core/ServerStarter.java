package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.core.IServerStarter;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.ServerStarterException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;

import java.util.List;

public class ServerStarter implements IServerStarter {

    private final ILogger log;
    private final IBootstrap bootstrap;
    private final IFeatureReader featureReader;
    private final IDependencyReplacer dependencyReplacer;
    private final IFeatureSorting featureSorting;
    private final IPluginLoader<List<IFeature>> pluginLoader;

    public ServerStarter(
            final ILogger log,
            final IBootstrap bootstrap,
            final IFeatureReader featureReader,
            final IDependencyReplacer dependencyReplacer,
            final IFeatureSorting featureSorting,
            final IPluginLoader<List<IFeature>> pluginLoader
    ) {
        this.log = log;
        this.bootstrap = bootstrap;
        this.featureReader = featureReader;
        this.dependencyReplacer = dependencyReplacer;
        this.featureSorting = featureSorting;
        this.pluginLoader = pluginLoader;

    }

    @Override
    public boolean initServerFeatures(
            final IClassLoaderWrapper classLoader,
            final List<IPath> coreFeatures
    ) throws ServerStarterException {
        try {
            List<IFeature> features = featureReader.readFeatures(coreFeatures);
            List<IFeature> sortedRawFeatures = featureSorting.sortFeatures(features);
            dependencyReplacer.replaceDependencies(sortedRawFeatures);
            List<IFeature> sortedFeatures = featureSorting.sortFeatures(sortedRawFeatures);

            pluginLoader.loadPlugins(sortedFeatures);

            try {
                log.info("Starting bootstrap...");
                bootstrap.start();
            } catch (Exception e) {
                try {
                    log.error("Failed to start bootstrap, reverting... Error: {0}", e.getCause().getCause().getCause().getLocalizedMessage());
                    bootstrap.revert();
                    return false;
                } catch (Exception ee) {
                    e.addSuppressed(ee);
                    return false;
                }
            }
            return true;
        } catch (FeatureSortingException e) {
            throw new ServerStarterException("Failed to sort features by dependencies", e);
        } catch (FeatureReaderException e) {
            throw new ServerStarterException("Failed to read features from jar files", e);
        } catch (PluginLoaderException e) {
            throw new ServerStarterException("Failed to load plugins in features", e);
        }
    }
}

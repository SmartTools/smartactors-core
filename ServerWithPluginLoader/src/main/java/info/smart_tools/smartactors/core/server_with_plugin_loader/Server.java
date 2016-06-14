package info.smart_tools.smartactors.core.server_with_plugin_loader;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.feature_manager.FeatureManager;
import info.smart_tools.smartactors.core.filesystem_tracker.FilesystemTracker;
import info.smart_tools.smartactors.core.filesystem_tracker.ListenerTask;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ifeature_manager.IFeature;
import info.smart_tools.smartactors.core.ifeature_manager.IFeatureManager;
import info.smart_tools.smartactors.core.ifilesystem_tracker.IFilesystemTracker;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.core.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.ExpansibleURLClassLoader;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.core.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;

import java.io.File;
import java.net.URL;

/**
 * Server with PluginLoader
 */
public class Server implements IServer {

    @Override
    public void initialize()
            throws ServerInitializeException {
        try {

            // Initialize plugin infrastructure
            IBootstrap bootstrap = new Bootstrap();
            IPluginCreator creator = new PluginCreator();
            IPluginLoaderVisitor<String> visitor = new PluginLoaderVisitor<>();
            ExpansibleURLClassLoader urlClassLoader = new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
            IPluginLoader<String> pluginLoader = new PluginLoader(
                    urlClassLoader,
                    (t) -> {
                        try {
                            IPlugin plugin = creator.create(t, bootstrap);
                            plugin.load();
                        } catch (Exception e) {
                            throw new RuntimeException("Could not create instance of IPlugin");
                        }
                    },
                    visitor
            );


            // FS listener creation
            // TODO: Get from configuration
            File coreJarsDir = new File("libs");
            coreJarsDir.mkdirs();

            IFilesystemTracker jarFilesTracker = new FilesystemTracker(
                    (dir, name) -> name.endsWith(".jar"),
                    ListenerTask::new);

            jarFilesTracker.start(coreJarsDir);

            // FeatureManager & Feature creation
            IFeatureManager featureManager = new FeatureManager(jarFilesTracker);

            IFeature coreFeature = featureManager.newFeature("smartactors.core");
            coreFeature.whenPresent(files -> {
                for (File file : files) {
                    try {
                        pluginLoader.loadPlugin(file.getAbsolutePath());
                    } catch (Throwable e) {
                        throw new RuntimeException("Plugin loading failed.");
                    }
                }
                try {
                    bootstrap.start();
                } catch (Throwable e) {
                    throw new RuntimeException("Could not execute plugin process");
                }
            });

            String[] coreJars = {
                    "ScopeProvider.jar",
                    "PluginSubscribeScopeProviderOnScopeCreation.jar"
            };
            for (String jarName : coreJars) {
                coreFeature.requireFile(jarName);
            }

            coreFeature.listen();

        } catch (Throwable e) {
            throw new ServerInitializeException("Server initialization failed.", e);
        }


    }

    @Override
    public void start() throws ServerExecutionException {

    }
}

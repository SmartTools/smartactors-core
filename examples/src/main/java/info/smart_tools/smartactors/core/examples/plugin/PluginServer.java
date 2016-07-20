package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.core.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.core.iserver.IServer;
import info.smart_tools.smartactors.core.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.core.iserver.exception.ServerInitializeException;
import info.smart_tools.smartactors.core.path.Path;
import info.smart_tools.smartactors.core.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.ExpansibleURLClassLoader;
import info.smart_tools.smartactors.core.plugin_loader_from_jar.PluginLoader;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

/**
 *  Sample server implementation.
 */
public class PluginServer implements IServer {

    @Override
    public void initialize() throws ServerInitializeException {
        try {
            IBootstrap bootstrap = new Bootstrap();         // initializes the server in correct order
            IPluginCreator creator = new PluginCreator();   // instantiates the plugin correctly
            IPluginLoaderVisitor<String> visitor = new SamplePluginVisitor();   // checks the plugin loadings

            ClassLoader urlClassLoader =
                    new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());  // loads plugins' classes

            IPluginLoader<Collection<IPath>> pluginLoader = new PluginLoader(
                    // loads plugins to classloader
                    urlClassLoader,
                    (t) -> {
                        try {
                            IPlugin plugin = creator.create(t, bootstrap);  // creates the plugin instance
                            plugin.load();                                  // loads the plugin
                        } catch (Exception e) {
                            throw new RuntimeException("Could not create instance of IPlugin", e);
                        }
                    },
                    visitor
            );

            Collection<IPath> fileCollection = new ArrayList<>();
            fileCollection.add(new Path("libs/SamplePlugin.jar"));
            fileCollection.add(new Path("libs/IocPlugin.jar"));
            pluginLoader.loadPlugin(fileCollection);     // loads plugins

            bootstrap.start();                           // starts initialization
        } catch (Throwable e) {
            throw new ServerInitializeException("Server initialization failed.", e);
        }
    }

    @Override
    public void start() throws ServerExecutionException {
    }
}

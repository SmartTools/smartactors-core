package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.bootstrap.Bootstrap;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
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

import java.io.File;
import java.net.URL;
import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 *  Sample server implementation.
 */
public class MyServer implements IServer {

    @Override
    public void initialize() throws ServerInitializeException {
        try {
            IBootstrap bootstrap = new Bootstrap();         // initializes the server in correct order
            IPluginCreator creator = new PluginCreator();   // instantiates the plugin correctly
            IPluginLoaderVisitor<String> visitor = new MyPluginVisitor();   // checks the plugin loadings

            ClassLoader urlClassLoader =
                    new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());  // loads plugins' classes

            IPluginLoader<Collection<File>> pluginLoader = new PluginLoader(
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

            File examplesJar = new File(System.getProperty("user.home"), ".m2/repository/info/smart_tools/smartactors/" +
                    "core.examples/1.0-SNAPSHOT/core.examples-1.0-SNAPSHOT.jar");
            Collection<File> fileCollection = new CopyOnWriteArrayList<>();
            fileCollection.add(examplesJar);
            pluginLoader.loadPlugin(fileCollection);     // loads plugins
            bootstrap.start();                                          // starts initialization
        } catch (Throwable e) {
            throw new ServerInitializeException("Server initialization failed.", e);
        }
    }

    @Override
    public void start() throws ServerExecutionException {
    }
}

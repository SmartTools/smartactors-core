package info.smart_tools.smartactors.servers.server2.server;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.base.path.Path;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.event_handler.event_handler.Event;
import info.smart_tools.smartactors.event_handler.event_handler.EventHandler;
import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.ProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.exception.RevertProcessExecutionException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.exception.PluginCreationException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.exception.PluginLoaderException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;
import info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_from_jar.PluginLoader;
import info.smart_tools.smartactors.feature_loading_system.plugin_loader_visitor_empty_implementation.PluginLoaderVisitor;
import info.smart_tools.smartactors.server_interfaces.interfaces.iserver.IServer;
import info.smart_tools.smartactors.server_interfaces.interfaces.iserver.exception.ServerExecutionException;
import info.smart_tools.smartactors.server_interfaces.interfaces.iserver.exception.ServerInitializeException;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class Server implements IServer {
    private IPluginLoaderVisitor<String> pluginLoaderVisitor = new PluginLoaderVisitor<>();
    private IPluginCreator pluginCreator = new PluginCreator();

    /**
     * Runs the server.
     *
     * @param args    command line arguments
     * @throws Exception if any error occurs
     */
    public static void main(final String[] args)
            throws Exception {
        IServer server = new Server();
        server.initialize();
        try {
            server.start();
        } catch (Exception e) {
            EventHandler.handle(
                    Event.builder().body(e).build()
            );
        }
    }

    @Override
    public void initialize()
            throws ServerInitializeException {
        Thread.currentThread().setName("BaseThread");
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
    }

    @Override
    public void start()
            throws ServerExecutionException {
        EventHandler.handle(
            Event
                .builder()
                .body(
                        String.format(
                                "\n%s\nStarting server ...",
                                (new Date()).toString()
                        )
                )
                .build()
        );
        loadCore();
    }

    private void loadCore()
            throws ServerExecutionException {
        try {
            LocalTime start = LocalTime.now();
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_TIME;
            File coreDir = new File("core");
            List<IPath> jars = new ArrayList<>();
            for (File file : coreDir.listFiles()) {
                if (file.isDirectory()) {
                    jars.addAll(getListOfJars(file));
                } else if (isJAR(file)) {
                    jars.add(new Path(file));
                }
            }
            loadPlugins(jars);
            Duration elapsedTime = Duration.between(start, LocalTime.now());
            LocalTime elapsedTimeToLocalTime = LocalTime.ofNanoOfDay(elapsedTime.toNanos());
            EventHandler.handle(
                    Event.builder().body("[OK] Stage 1: server core has been loaded successful.").build()
            );
            EventHandler.handle(
                    Event.builder().body("[OK] Stage 1: elapsed time - " + elapsedTimeToLocalTime.format(df) + ".").build())
            ;
            EventHandler.handle(
                    Event.builder().body("\n\n").build()
            );
        } catch (IOException | InvalidArgumentException | PluginLoaderException | ProcessExecutionException e) {
            throw new ServerExecutionException(e);
        }
    }

    private List<IPath> getListOfJars(final File directory)
            throws IOException {
        if (!directory.isDirectory()) {
            throw new IOException(
                    MessageFormat.format(
                            "File ''{0}'' is not a directory.",
                            directory.getAbsolutePath()
                    )
            );
        }
        File[] files = directory.listFiles(this::isJAR);
        List<IPath> paths = new LinkedList<>();
        for (File file : files) {
            paths.add(new Path(file));
        }

        return paths;
    }

    private void loadPlugins(final List<IPath> jars)
            throws InvalidArgumentException, PluginLoaderException, ProcessExecutionException {
        IBootstrap bootstrap = new Bootstrap();
        IPluginLoader<Collection<IPath>> pluginLoader = new PluginLoader(
                ModuleManager.getCurrentClassLoader(),
                clz -> {
                    try {
                        if (Modifier.isAbstract(clz.getModifiers())) {
                            return;
                        }
                        IPlugin plugin = pluginCreator.create(clz, bootstrap);
                        plugin.load();
                    } catch (PluginCreationException | PluginException e) {
                        throw new ActionExecutionException(e);
                    }
                },
                pluginLoaderVisitor
        );
        pluginLoader.loadPlugins(jars);
        try {
            bootstrap.start();
        } catch (ProcessExecutionException e) {
            try {
                bootstrap.revert();
            } catch (RevertProcessExecutionException ee) {
                e.addSuppressed(ee);
            }
            throw e;
        }
    }

    private boolean isJAR(final File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }
}

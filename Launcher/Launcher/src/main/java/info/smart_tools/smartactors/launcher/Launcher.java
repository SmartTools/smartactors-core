package info.smart_tools.smartactors.launcher;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.launcher.bootstrap.BootstrapWrapper;
import info.smart_tools.smartactors.launcher.classloader.SmartactorsClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.feature.FeatureReader;
import info.smart_tools.smartactors.launcher.feature.FeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.ILauncher;
import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.LauncherExecutionException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ibootstrapwrapper.BootstrapWrapperInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreatorInitException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderInitException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrapwrapper.IBootstrapWrapper;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginLoader;
import info.smart_tools.smartactors.launcher.objectmapper.ObjectMapper;
import info.smart_tools.smartactors.launcher.path.Path;
import info.smart_tools.smartactors.launcher.plugin.PluginCreator;
import info.smart_tools.smartactors.launcher.plugin.PluginLoader;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.MessageFormat;
import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 *
 */
public class Launcher implements ILauncher {
    /**
     * Runs the server.
     *
     * @param args command line arguments
     * @throws Exception if any error occurs
     */
    public static void main(final String[] args) throws Exception {
        System.out.println("Initializing launcher...");
        Thread.sleep(5000);
        ILauncher launcher = new Launcher();
        launcher.initialize();
        launcher.start();
    }

    public void initialize() {
        Thread.currentThread().setName("BaseThread");
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
    }

    public void start() throws LauncherExecutionException {
        try {
            loadCore();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCore() throws LauncherExecutionException {
        try {
            System.out.println("[OK] Starting to load core 1");
            LocalTime start = LocalTime.now();
            DateTimeFormatter df = DateTimeFormatter.ISO_LOCAL_TIME;

            File coreDir = new File("core");
            List<IPath> jars = new ArrayList<>();
            // TODO: handle null pointer
            for (File file : coreDir.listFiles()) {
                if (file.isDirectory()) {
                    jars.addAll(getListOfJars(file));
                } else if (isJAR(file)) {
                    jars.add(new Path(file));
                }
            }

            File dependencies = new File("./launcher/launcher_dependencies.jar");

            ClassLoader jcl = URLClassLoader.newInstance(new URL[]{
                    dependencies.toURI().toURL()
            });
            ISmartactorsClassLoader cl = ModuleManager.getCurrentClassLoader();
            IObjectMapper objectMapper = ObjectMapper.newInstance(jcl);
            IClassLoaderWrapper classLoader = SmartactorsClassLoaderWrapper.newInstance(cl);
            System.out.println("[OK] Initialized class loader and object mapper");

            loadDependencies(jars, classLoader);
            loadPlugins(jars, classLoader, objectMapper);

            Duration elapsedTime = Duration.between(start, LocalTime.now());
            LocalTime elapsedTimeToLocalTime = LocalTime.ofNanoOfDay(elapsedTime.toNanos());

            System.out.println("\n\n");
            System.out.println("[OK] Stage 1: server core 1 has been loaded successful.");
            System.out.println("[OK] Stage 1: elapsed time - " + elapsedTimeToLocalTime.format(df) + ".");
            System.out.println("\n\n");
        } catch (Exception e) {
            throw new LauncherExecutionException(e);
        }
    }

    private List<IPath> getListOfJars(final File directory) throws IOException {
        if (!directory.isDirectory()) {
            throw new IOException(MessageFormat.format("File ''{0}'' is not a directory.", directory.getAbsolutePath()));
        }

        File[] files = directory.listFiles(this::isJAR);
        List<IPath> paths = new LinkedList<>();
        // TODO: handle null pointer
        for (File file : files) {
            paths.add(new Path(file));
        }

        return paths;
    }

    private void loadDependencies(
            final List<IPath> jars,
            final IClassLoaderWrapper classLoader
    ) throws MalformedURLException {
        for (IPath file : jars) {
            URL url = new URL("jar:file:" + file.getPath() + "!/");
            classLoader.addURL(url);
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
        IBootstrapWrapper bootstrap = new BootstrapWrapper(classLoader);
        IPluginCreator pluginCreator = new PluginCreator(classLoader);
        IFeatureReader featureReader = new FeatureReader(objectMapper);
        IFeatureSorting featureSorting = new FeatureSorting();
        IPluginLoader<List<IFeature>> pluginLoader = new PluginLoader(
                classLoader,
                bootstrap,
                pluginCreator
        );

        // TODO: perform check on possibly empty list
        List<IFeature> features = featureReader.readFeatures(jars);
        List<IFeature> sortedFeatures = featureSorting.sortFeatures(features);

        pluginLoader.loadPlugins(sortedFeatures, objectMapper);

        try {
            System.out.println("[OK] Starting bootstrap...");
            bootstrap.start();
        } catch (Exception e) {
            try {
//                System.out.println("\n\n[ERROR] Failed to load feature \"" + feature.getName() + "\", reason: " + e.getCause().getCause().getLocalizedMessage() + "\n\n");
                System.out.println("[ERROR] Failed to start bootstrap, reverting... Error: " + e.getCause().getCause().getCause().getLocalizedMessage());
                bootstrap.revert();
            } catch (Exception ee) {
                e.addSuppressed(ee);
            }
        }
    }

    private boolean isJAR(final File file) {
        return file.isFile() && file.getName().endsWith(".jar");
    }
}

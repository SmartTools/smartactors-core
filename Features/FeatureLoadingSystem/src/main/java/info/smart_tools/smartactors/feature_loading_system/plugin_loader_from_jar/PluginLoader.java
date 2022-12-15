package info.smart_tools.smartactors.feature_loading_system.plugin_loader_from_jar;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.ipath.IPath;
import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader.exception.PluginLoaderException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_loader_visitor.IPluginLoaderVisitor;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Implementation of {@link IPluginLoader}.
 * <pre>
 * Main features of implementation:
 * - find implementation of {@link IPlugin} in given Jar file.
 * - implementation of {@link IPlugin} should be has constructor with
 * given parameters.
 * </pre>
 */
public class PluginLoader implements IPluginLoader<Collection<IPath>> {

    private static final String CLASS_EXTENSION = ".class";

    /** ClassLoader for load classes*/
    private ISmartactorsClassLoader classLoader;

    /** Action to create instance of given class */
    private IAction<Class> creator;

    /** Visitor contains some handlers to handle success or fail loading execution */
    private IPluginLoaderVisitor<String> visitor;

    /**
     * Constructor with two arguments
     * @param classLoader instance of {@link ClassLoader}
     * @param action instance of {@link IAction}
     * @param visitor instance of {@link IPluginLoaderVisitor}
     * @throws InvalidArgumentException if incoming argument are wrong
     */
    public PluginLoader(final ISmartactorsClassLoader classLoader, final IAction<Class> action, final IPluginLoaderVisitor<String> visitor)
            throws InvalidArgumentException {
        if (null == action || null == classLoader || null == visitor) {
            throw new InvalidArgumentException("Incoming argument should not be null.");
        }
        this.creator = action;
        this.visitor = visitor;
        try {
            this.classLoader = classLoader;
        } catch (Throwable e) {
            throw new InvalidArgumentException("Could not cast given ClassLoader to the URLClassLoader.");
        }
    }

    @Override
    public void loadPlugins(final Collection<IPath> files)
            throws PluginLoaderException {
        try {
            for (IPath file : files) {
                URL url = new URL("jar:file:" + file.getPath() + "!/");
                this.classLoader.addURL(url);
            }
        } catch (Throwable e) {
            throw new PluginLoaderException("Malformed file name.", e);
        }

        JarFile jarFile = null;
        String pathToJar = null;
        for (IPath file : files) {
            try {
                pathToJar = file.getPath();
                jarFile = new JarFile(pathToJar);
                Enumeration<JarEntry> iterator = jarFile.entries();
                while (iterator.hasMoreElements()) {
                    JarEntry je = iterator.nextElement();
                    if (je.isDirectory() || !je.getName().endsWith(CLASS_EXTENSION)) {
                        continue;
                    }
                    String className = je.getName().substring(0, je.getName().length() - CLASS_EXTENSION.length());
                    className = className.replace('/', '.');
                    Class clazz;
                    try {
                        clazz = classLoader.loadClass(className);
                    } catch (Throwable e) {
                        // ignoring, because the plugin which class cannot be loaded cannot be loaded
                        System.out.println("[\033[1;33mWARNING\033[0m] Class "+className+" loading failed.");
                        continue;
                    }

                    if (IPlugin.class.isAssignableFrom(clazz) && clazz != IPlugin.class) {
                        creator.execute(clazz);
                    }
                }
            } catch (Throwable e) {
                visitor.pluginLoadingFail(pathToJar, e);
                throw new PluginLoaderException("Plugin loading failed: " + pathToJar, e);
            } finally {
                if (null != jarFile) {
                    try {
                        jarFile.close();
                    } catch (IOException e) {
                        //TODO: replace throw by other logic
                        throw new PluginLoaderException("Error on close instance of JarFile .", e);
                    }
                }
            }
        }
    }
}

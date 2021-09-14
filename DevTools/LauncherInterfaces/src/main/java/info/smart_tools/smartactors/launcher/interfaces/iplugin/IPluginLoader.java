package info.smart_tools.smartactors.launcher.interfaces.iplugin;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginLoaderException;

/**
 * Interface IPluginLoader
 * @param <T> type of plugin package
 */
public interface IPluginLoader<T> {

    /**
     * Load plugin to the system
     * @param plugin plugin package (for example, jar file)
     * @throws PluginLoaderException if any errors occurred
     */
    void loadPlugins(T plugin) throws PluginLoaderException;
}

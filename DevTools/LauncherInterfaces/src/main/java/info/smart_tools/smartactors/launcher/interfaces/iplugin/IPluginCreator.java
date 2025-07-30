package info.smart_tools.smartactors.launcher.interfaces.iplugin;

import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreationException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;

/**
 * IPluginCreator provides functionality to create instance of plugin
 */
public interface IPluginCreator {

    /**
     * Create new instance of plugin
     *
     * @param clz plugin's class
     * @param bootstrap bootstrap wrapper
     * @return instance of plugin
     * @throws PluginCreationException if failed to create instance of plugin
     */
    Object create(
            Class<?> clz,
            IBootstrap bootstrap
    ) throws PluginCreationException;
}

package info.smart_tools.smartactors.core.iplugin;

import info.smart_tools.smartactors.core.iplugin.exception.PluginException;

/**
 * IPlugin interface
 */
public interface IPlugin {

    /**
     * Implementation of this method should be contains rules for loading plugin to the system
     * (dependencies, actions for loading plugin components)
     * @throws PluginException if any errors occurred
     */
    void load()
            throws PluginException;
}

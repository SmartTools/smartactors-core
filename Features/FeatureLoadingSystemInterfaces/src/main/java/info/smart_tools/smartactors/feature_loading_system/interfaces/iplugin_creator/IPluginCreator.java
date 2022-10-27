package info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.exception.PluginCreationException;

/**
 * IPluginCreator interface.
 * Implementation of this interface should be create instance
 * of {@link IPlugin}.
 */
public interface IPluginCreator {

    /**
     * Create instance of {@link IPlugin}
     * @param clazz the basis for instance creation
     * @param bootstrap the incoming argument
     * @throws PluginCreationException if any errors occured
     * @return instance of {@link IPlugin}
     */
    IPlugin create(Class clazz, IBootstrap bootstrap)
            throws PluginCreationException;
}

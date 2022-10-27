package info.smart_tools.smartactors.feature_loading_system.plugin_creator;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.IPluginCreator;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.exception.PluginCreationException;

import java.lang.reflect.Constructor;

/**
 * Implementation {@link IPluginCreator}
 */
public class PluginCreator implements IPluginCreator {

    @Override
    public IPlugin create(final Class clazz, final IBootstrap bootstrap)
            throws PluginCreationException {
        try {
            Constructor c = clazz.getDeclaredConstructor(new Class[]{IBootstrap.class});
            return (IPlugin) c.newInstance(new Object[]{bootstrap});
        } catch (Throwable e) {
            throw new PluginCreationException("Could not create instance of IPlugin", e);
        }
    }
}

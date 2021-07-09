package info.smart_tools.smartactors.launcher.plugin;


import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreationException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreatorInitException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;

import java.lang.reflect.Method;

public class PluginCreator implements IPluginCreator {

    private final Class<?> pluginCreatorClass;
    private final Class<?> bootstrapInterface;

    private final Object pluginCreator;

    public PluginCreator(
            final IClassLoaderWrapper classLoader
    ) throws PluginCreatorInitException {
        try {
            this.pluginCreatorClass = classLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator");
            this.bootstrapInterface = classLoader.loadClass("info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap");

            this.pluginCreator = pluginCreatorClass.newInstance();
        } catch (Exception e) {
            throw new PluginCreatorInitException("Failed to initialize plugin creator", e);
        }
    }

    @Override
    public Object create(
            final Class<?> clz,
            final IBootstrap bootstrapWrapper
    ) throws PluginCreationException {
        try {
            Object bootstrap = bootstrapWrapper.getInstance();

            Method createPlugin = pluginCreatorClass.getMethod("create", Class.class, bootstrapInterface);
            return createPlugin.invoke(pluginCreator, clz, bootstrap);
        } catch (Exception e) {
            throw new PluginCreationException("Failed to create plugin", e);
        }
    }
}

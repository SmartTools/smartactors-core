package info.smart_tools.smartactors.launcher.interfaces.iplugin;

import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreationException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;

public interface IPluginCreator {

    Object create(
            Class<?> clz,
            IBootstrap bootstrapWrapper
    ) throws PluginCreationException;
}

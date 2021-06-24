package info.smart_tools.smartactors.launcher.interfaces.iplugin;

import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreationException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrapwrapper.IBootstrapWrapper;

public interface IPluginCreator {

    Object create(
            Class<?> clz,
            IBootstrapWrapper bootstrapWrapper
    ) throws PluginCreationException;
}

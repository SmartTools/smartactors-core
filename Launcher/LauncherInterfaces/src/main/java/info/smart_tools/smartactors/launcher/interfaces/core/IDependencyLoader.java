package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.DependencyLoaderException;

import java.util.List;

public interface IDependencyLoader {

    void load(IClassLoaderWrapper classLoader, List<IPath> coreFeatures) throws DependencyLoaderException;
}

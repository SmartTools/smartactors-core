package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.DependencyLoaderException;

import java.util.List;

/**
 * Interface IDependencyLoader
 */
public interface IDependencyLoader {

    /**
     * Load features to class loader
     *
     * @param classLoader class loader wrapper
     * @param coreFeatures list of features to load
     * @throws DependencyLoaderException if failed to load features to class loader
     */
    void load(IClassLoaderWrapper classLoader, List<IPath> coreFeatures) throws DependencyLoaderException;
}

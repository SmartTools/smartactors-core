package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.CoreLoaderException;

import java.util.List;

/**
 * Interface ICoreLoader
 */
public interface ICoreLoader {

    /**
     * Load core features from filesystem
     *
     * @param coreFeaturesPath path to folder with core features
     * @return list of paths to core features
     * @throws CoreLoaderException if failed to load core features
     */
    List<IPath> loadCoreFeature(String coreFeaturesPath) throws CoreLoaderException;
}

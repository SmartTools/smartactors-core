package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.FeatureLoaderException;

import java.util.List;

/**
 * Interface ICoreLoader
 */
public interface IFeatureLoader {

    /**
     * Load core features from filesystem
     *
     * @param coreFeaturesPath path to folder with core features
     * @return list of paths to core features
     * @throws FeatureLoaderException if failed to load core features
     */
    List<IPath> loadFeatures(String coreFeaturesPath) throws FeatureLoaderException;
}

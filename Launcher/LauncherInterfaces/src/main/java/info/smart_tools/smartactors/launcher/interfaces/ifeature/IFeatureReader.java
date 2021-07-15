package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;

import java.util.List;

/**
 * Interface for feature reader
 */
public interface IFeatureReader {

    /**
     * Read features from filesystem
     * @param paths list of paths to files with features
     * @return list of features
     * @throws FeatureReaderException if failed to read features from filesystem
     */
    List<IFeature> readFeatures(List<IPath> paths) throws FeatureReaderException;
}

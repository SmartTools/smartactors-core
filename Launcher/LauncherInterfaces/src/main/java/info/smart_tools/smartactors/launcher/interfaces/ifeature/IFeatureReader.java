package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;

import java.util.List;

public interface IFeatureReader {

    List<IFeature> readFeatures(List<IPath> jars) throws FeatureReaderException;
}

package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;

import java.util.List;

public interface IFeature {

    Object getId();
    String getFileName();
    IPath getPath();
    String getName();
    List<String> getAfterFeatures();
    void setAfterFeatures(List<String> afterFeatures);
    List<String> getPlugins();
}

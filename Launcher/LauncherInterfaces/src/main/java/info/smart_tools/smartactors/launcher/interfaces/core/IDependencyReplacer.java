package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;

import java.util.List;

public interface IDependencyReplacer {

    void replaceDependencies(List<IFeature> features);
}

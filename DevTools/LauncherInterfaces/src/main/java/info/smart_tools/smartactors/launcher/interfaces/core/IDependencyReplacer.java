package info.smart_tools.smartactors.launcher.interfaces.core;

import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;

import java.util.List;

/**
 * Interface IDependencyReplacer
 */
public interface IDependencyReplacer {

    /**
     * Replace features with plugins in features
     *
     * @param features list of features with dependencies to replace
     */
    void replaceDependencies(List<IFeature> features);
}

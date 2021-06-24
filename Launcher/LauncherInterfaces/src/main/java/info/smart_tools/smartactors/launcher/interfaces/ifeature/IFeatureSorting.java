package info.smart_tools.smartactors.launcher.interfaces.ifeature;

import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;

import java.util.List;

public interface IFeatureSorting {

    List<IFeature> sortFeatures(List<IFeature> features) throws FeatureSortingException;
}

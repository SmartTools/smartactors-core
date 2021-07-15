package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import info.smart_tools.smartactors.launcher.sort.TopologicalSort;

import java.util.List;

public class FeatureSorting implements IFeatureSorting {

    @Override
    public List<IFeature> sortFeatures(final List<IFeature> features) throws FeatureSortingException {
        try {
            TopologicalSort<IFeature> ts = new TopologicalSort<>(features);
            return ts.getOrderedList(false);
        } catch (Exception e) {
            throw new FeatureSortingException("Failed to sort features", e);
        }
    }
}

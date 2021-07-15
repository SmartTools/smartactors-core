package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;

import java.util.List;

public class DependencyReplacer implements IDependencyReplacer {

    @Override
    public void replaceDependencies(
            final List<IFeature> features
    ) {
        for (IFeature dependency : features) {
            String featureName = dependency.getName();
            List<String> plugins = dependency.getPlugins();

            if (plugins == null || plugins.isEmpty()) {
                continue;
            }

            for (IFeature feature : features) {
                if (feature.getName().contains(featureName)) {
                    continue;
                }
                if (feature.getPlugins() != null && feature.getPlugins().contains(featureName)) {
                    continue;
                }
                if (plugins.contains(feature.getName())) {
                    continue;
                }

                List<String> afterFeatures = feature.getAfterFeatures();
                if (afterFeatures.remove(featureName)) {
                    afterFeatures.addAll(plugins);
                    feature.setAfterFeatures(afterFeatures);
                }
            }
        }
    }
}

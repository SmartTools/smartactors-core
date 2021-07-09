package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.feature.Feature;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.logger.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DependencyReplacer implements IDependencyReplacer {

    private static final ILogger log = LoggerFactory.getLogger();

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

    private List<IFeature> oldReplace(
            final List<IFeature> features
    ) {
        List<IFeature> updatedFeatures = new ArrayList<>();

        for (IFeature feature : features) {
            List<String> afterFeatures = feature.getAfterFeatures();
            List<String> updatedAfterFeatures = new ArrayList<>();

            for (String af : afterFeatures) {
                IFeature afterFeature = features.stream()
                        .filter(f -> f.getName().equals(af))
                        .findFirst()
                        .orElse(null);

                if (null == afterFeature) {
                    updatedAfterFeatures.add(af);
                    continue;
                }

                List<String> plugins = afterFeature.getPlugins();
                if (null == plugins || plugins.isEmpty()) {
                    updatedAfterFeatures.add(af);
                    continue;
                }

                List<String> filteredPlugins = plugins.stream()
                        .map(p -> {
                            if (p.contains(feature.getName())) {
                                return af;
                            } else {
                                return p;
                            }
                        })
                        .collect(Collectors.toList());
                updatedAfterFeatures.addAll(filteredPlugins);
            }

            updatedFeatures.add(new Feature(
                    feature.getId(),
                    feature.getFileName(),
                    feature.getName(),
                    updatedAfterFeatures,
                    feature.getPlugins()
            ));
        }

        return updatedFeatures;
    }
}

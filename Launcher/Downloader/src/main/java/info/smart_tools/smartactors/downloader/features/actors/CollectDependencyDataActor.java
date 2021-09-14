package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.actors.ConditionalInterface;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.downloader.features.Feature;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CollectDependencyDataActor extends StatelessActor {

    private List<Feature> loadingList;
    private Set<String> featureNames;

    @Executable()
    public ConditionalInterface configure(
            final Feature feature
    ) {
        this.loadingList = new ArrayList<>();
        this.featureNames = new HashSet<>();
        this.featureNames.add(feature.getFeatureName());
        if (null != feature.getAfterFeatures() && !feature.getAfterFeatures().isEmpty()) {
            this.loadingList.addAll(feature.getAfterFeatures().stream().map(Feature::new).collect(Collectors.toList()));
        }
        if (null != feature.getPlugins() && !feature.getPlugins().isEmpty()) {
            this.loadingList.addAll(feature.getPlugins().stream().map(Feature::new).collect(Collectors.toList()));
        }
        System.out.println("Start to collect feature configs.");
        return () -> loadingList.size() > 0;
    }

    @Executable()
    public Feature processNextFeature() {
        try {
            return this.loadingList.remove(0);
        } catch (Exception e) {
            throw new RuntimeException("Could not gather configs of features.", e);
        }
    }

    @Executable()
    public void addNewFeatures(final Feature feature) {
        if (null != feature) {
            this.featureNames.add(feature.getFeatureName());
            if (null != feature.getAfterFeatures() && !feature.getAfterFeatures().isEmpty()) {
                List<Feature> features = feature.getAfterFeatures().stream().filter(
                        f -> !this.featureNames.contains(f)
                ).map(Feature::new).collect(Collectors.toList());
                this.loadingList.addAll(features);
                featureNames.addAll(features.stream().map(Feature::getFeatureName).collect(Collectors.toList()));
            }
            if (null != feature.getPlugins() && !feature.getPlugins().isEmpty()) {
                List<Feature> features =feature.getPlugins().stream().filter(
                        f -> !this.featureNames.contains(f)
                ).map(Feature::new).collect(Collectors.toList());
                this.loadingList.addAll(features);
                featureNames.addAll(features.stream().map(Feature::getFeatureName).collect(Collectors.toList()));
            }
        }
    }
}

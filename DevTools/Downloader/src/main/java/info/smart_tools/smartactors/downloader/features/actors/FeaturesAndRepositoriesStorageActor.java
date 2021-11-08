package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.downloader.Params__DownloadFeature;
import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.Feature;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FeaturesAndRepositoriesStorageActor extends StatelessActor {

    @Executable
    public Map<String, Object> configure() {
        Map<String, Object> response = new HashMap();
        response.put(Params__DownloadFeature.REPOSITORIES, new ArrayList<Repository>());
        response.put(Params__DownloadFeature.FEATURES, new ArrayList<Feature>());
        return response;
    }

    @Executable
    public void updateStorages(final Feature feature, final List<Feature> features, final List<Repository> repositories) {
        if (null != feature) {
            if (
                    null != features &&
                            !features.stream().map(Feature::getFeatureName).collect(Collectors.toList()).contains(feature.getFeatureName())
            ) {
                features.add(feature);
            }
            if (null != feature.getRepository() && null != repositories) {
                if (!repositories.stream().map(Repository::getId).collect(Collectors.toList()).contains(feature.getRepository().getId())) {
                    repositories.add(feature.getRepository());
                }
            }
            if (null != feature.getDependencyRepositories() && !feature.getDependencyRepositories().isEmpty() && null != repositories) {
                feature.getDependencyRepositories().forEach(
                        dr -> {
                            if (
                                    !repositories
                                            .stream()
                                            .map(Repository::getId)
                                            .collect(Collectors.toList()).contains(dr.getId())
                            ) {
                                repositories.add(dr);
                            }
                        }
                );
            }
        }
    }
}

package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.commons.CommonParameters;
import info.smart_tools.smartactors.downloader.commons.Executable;
import info.smart_tools.smartactors.downloader.commons.StatelessActor;
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
        response.put(CommonParameters.REPOSITORIES, new ArrayList<Repository>());
        response.put(CommonParameters.FEATURES, new ArrayList<Feature>());
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
        }
    }
}

package info.smart_tools.smartactors.uploader.repositories.actors;

import info.smart_tools.simpleactors.commons.StatelessActor;
import info.smart_tools.simpleactors.commons.annotations.Executable;
import info.smart_tools.smartactors.uploader.features.Feature;
import info.smart_tools.smartactors.uploader.features.FeatureRepository;
import info.smart_tools.smartactors.uploader.repositories.RepositoryConfig;

public class CreateRepositoryConfigActor extends StatelessActor {

    @Executable
    public RepositoryConfig createConfig(final Feature feature, final String repositoryId, final String repositoryUrl) {
        if (repositoryId != null && repositoryUrl != null) {
            return new RepositoryConfig(repositoryId, repositoryUrl);
        }
        if (feature == null || feature.getRepository() == null) {
            throw new RuntimeException("Feature is null or does not contain credentials");
        }

        FeatureRepository repository = feature.getRepository();
        return new RepositoryConfig(repository.getId(), repository.getUrl());
    }
}

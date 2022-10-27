package info.smart_tools.smartactors.uploader.repositories.actors;

import info.smart_tools.smartactors.uploader.features.Feature;
import info.smart_tools.smartactors.uploader.features.FeatureRepository;
import info.smart_tools.smartactors.uploader.repositories.RepositoryConfig;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CreateRepositoryConfigActorTest {

    @Test
    public void createRepositoryConfigActor__createRepositoryConfigFromFeature() {
        FeatureRepository featureRepository = new FeatureRepository();
        featureRepository.setId("repository id");
        featureRepository.setUrl("repository url");
        Feature feature = new Feature();
        feature.setRepository(featureRepository);

        CreateRepositoryConfigActor actor = new CreateRepositoryConfigActor();
        RepositoryConfig config = actor.createConfig(feature, null, null);

        assertEquals("repository id", config.getRepositoryId());
        assertEquals("repository url", config.getRepositoryUrl());
    }

    @Test
    public void createRepositoryConfigActor__createRepositoryConfigFromParams() {
        FeatureRepository featureRepository = new FeatureRepository();
        featureRepository.setId("repository id");
        featureRepository.setUrl("repository url");
        Feature feature = new Feature();
        feature.setRepository(featureRepository);

        CreateRepositoryConfigActor actor = new CreateRepositoryConfigActor();
        RepositoryConfig config = actor.createConfig(feature, "new id", "new url");

        assertEquals("new id", config.getRepositoryId());
        assertEquals("new url", config.getRepositoryUrl());
    }

    @Test(expected = RuntimeException.class)
    public void createRepositoryConfigActor__throwExceptionWhenNullsAreProvided() {
        CreateRepositoryConfigActor actor = new CreateRepositoryConfigActor();
        actor.createConfig(null, null, null);
    }
}

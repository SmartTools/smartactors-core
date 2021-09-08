package info.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.actors.ReadFeatureConfigActor;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class ReadFeatureConfigActorTests {

    @Test
    public void creating() {
        ReadFeatureConfigActor actor = new ReadFeatureConfigActor();
        assertNotNull(actor);
    }

    @Test
    public void reading() {
        ReadFeatureConfigActor actor = new ReadFeatureConfigActor();
        assertNotNull(actor);
        String resourceName = "feature_config.json";
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource(resourceName).getFile());
        Feature feature = actor.readFeatureConfig(file.toPath().toAbsolutePath());
        assertNotNull(feature);
        assertEquals("info.smart_tools.smartactors:TestFeature1:0.7.0", feature.getFeatureName());
        if (null == feature.getAfterFeatures() || feature.getAfterFeatures().size() != 1) {
            fail();
        }
        if (null == feature.getPlugins() || feature.getPlugins().size() != 1) {
            fail();
        }
        assertEquals("info.smart_tools.smartactors:TestFeature2:0.7.0", feature.getAfterFeatures().get(0));
        assertEquals("info.smart_tools.smartactors:TestFeature1Plugin:0.7.0", feature.getPlugins().get(0));
        Repository repository = feature.getRepository();
        assertEquals("id", repository.getId());
        assertEquals("https://smartactors.com", repository.getUrl().toString());

        actor.readFeatureConfig(file.toPath().toAbsolutePath());
    }

    @Test(expected = RuntimeException.class)
    public void readingNegativeCase() {
        ReadFeatureConfigActor actor = new ReadFeatureConfigActor();
        assertNotNull(actor);
        List<Feature> features = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        Feature feature = actor.readFeatureConfig(Paths.get("src").toAbsolutePath()/*, features, repositories*/);
        fail();
    }
}

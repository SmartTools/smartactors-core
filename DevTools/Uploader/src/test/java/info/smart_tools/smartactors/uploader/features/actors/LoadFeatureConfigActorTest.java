package info.smart_tools.smartactors.uploader.features.actors;

import info.smart_tools.smartactors.uploader.features.Feature;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoadFeatureConfigActorTest {

    @Test
    public void loadFeatureConfig__loadListOfFeaturesFromResources() {
        List<File> featureFiles = new ArrayList<>();
        featureFiles.add(loadFile("features/simple-feature-0.7.0.jar"));
        featureFiles.add(loadFile("features/simple-feature-0.7.0.zip"));
        featureFiles.add(loadFile("features/simple-feature-0.7.0-sources.jar"));

        LoadFeatureConfigActor actor = new LoadFeatureConfigActor();

        Feature featureConfig = actor.loadFeatureConfig(featureFiles);

        assertNotNull(featureConfig);
        assertEquals("info.smart_tools.smartactors:simple-feature:0.7.0", featureConfig.getFeatureName());
        assertTrue(featureConfig.getAfterFeatures().contains("info.smart_tools.smartactors:core-service-starter:0.7.0"));
        assertTrue(featureConfig.getPlugins().contains("info.smart_tools.smartactors:simple-feature-plugin:0.7.0"));
        assertNotNull(featureConfig.getRepository());
        assertEquals("smartactors_core_and_core_features_dev", featureConfig.getRepository().getId());
        assertEquals("https://repository.smart-tools.info/artifactory", featureConfig.getRepository().getUrl());
    }

    @Test
    public void loadFeatureConfig__loadOnlyZipFile() {
        List<File> featureFiles = new ArrayList<>();
        featureFiles.add(loadFile("features/simple-feature-0.7.0.zip"));

        LoadFeatureConfigActor actor = new LoadFeatureConfigActor();

        Feature featureConfig = actor.loadFeatureConfig(featureFiles);

        assertNull(featureConfig);
    }

    @Test
    public void loadFeatureConfig__loadOnlySources() {
        List<File> featureFiles = new ArrayList<>();
        featureFiles.add(loadFile("features/simple-feature-0.7.0-sources.jar"));

        LoadFeatureConfigActor actor = new LoadFeatureConfigActor();

        Feature featureConfig = actor.loadFeatureConfig(featureFiles);

        assertNotNull(featureConfig);
        assertEquals("info.smart_tools.smartactors:simple-feature:0.7.0", featureConfig.getFeatureName());
        assertTrue(featureConfig.getAfterFeatures().contains("info.smart_tools.smartactors:core-service-starter:0.7.0"));
        assertTrue(featureConfig.getPlugins().contains("info.smart_tools.smartactors:simple-feature-plugin:0.7.0"));
        assertNotNull(featureConfig.getRepository());
        assertEquals("smartactors_core_and_core_features_dev", featureConfig.getRepository().getId());
        assertEquals("https://repository.smart-tools.info/artifactory", featureConfig.getRepository().getUrl());
    }

    @Test(expected = RuntimeException.class)
    public void loadFeatureConfig__tryToLoadInvalidPath() {
        List<File> featureFiles = new ArrayList<>();
        featureFiles.add(loadFile("features/simple-feature-0.7.0-src.jar"));

        LoadFeatureConfigActor actor = new LoadFeatureConfigActor();

        actor.loadFeatureConfig(featureFiles);
    }

    private File loadFile(final String file) {
        return new File(getClass().getClassLoader().getResource(file).getPath());
    }
}

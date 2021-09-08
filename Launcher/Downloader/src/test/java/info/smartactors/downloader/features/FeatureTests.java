package info.smartactors.downloader.features;

import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.features.Feature;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FeatureTests {

    @Test
    public void creating() {
        Feature feature = new Feature("test");
        assertEquals("test", feature.getFeatureName());

        feature = new Feature();
        assertNull(feature.getFeatureName());
    }

    @Test
    public void modification() {
        String featureName = "test";
        List<String> afterFeatures = new ArrayList<>();
        List<String> plugins = new ArrayList<>();
        Repository repository = new Repository();
        Feature feature = new Feature();
        feature.setFeatureName(featureName);
        feature.setAfterFeatures(afterFeatures);
        feature.setPlugins(plugins);
        feature.setRepository(repository);

        assertEquals(featureName, feature.getFeatureName());
        assertEquals(afterFeatures, feature.getAfterFeatures());
        assertEquals(plugins, feature.getPlugins());
        assertEquals(repository, feature.getRepository());
    }
}

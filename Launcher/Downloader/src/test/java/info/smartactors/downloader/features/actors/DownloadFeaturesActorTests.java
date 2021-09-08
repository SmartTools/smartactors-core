package info.smartactors.downloader.features.actors;

import info.smart_tools.smartactors.downloader.commons.actors.ConditionalInterface;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.actors.CollectDependencyDataActor;
import info.smart_tools.smartactors.downloader.features.actors.DownloadFeaturesActor;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DownloadFeaturesActorTests {

    @Test
    public void creating() {
        DownloadFeaturesActor actor = new DownloadFeaturesActor();
        assertNotNull(actor);
        List<Feature> features = new ArrayList<>();
        ConditionalInterface conditionalInterface = actor.configure(features);
        assertFalse(conditionalInterface.compare());
    }

    @Test
    public void processPositive() {
        DownloadFeaturesActor actor = new DownloadFeaturesActor();
        List<Feature> features = new ArrayList<>();
        Feature feature = new Feature();
        feature.setAfterFeatures(Arrays.asList("a", "b"));
        feature.setPlugins(Arrays.asList("c", "d"));
        features.add(feature);
        ConditionalInterface conditionalInterface1 = actor.configure(features);
        assertTrue(conditionalInterface1.compare());
        Feature processFeature = actor.nextFeature();
        assertEquals(feature, processFeature);
        assertFalse(conditionalInterface1.compare());
    }

    @Test(expected = RuntimeException.class)
    public void processNegative() {
        DownloadFeaturesActor actor = new DownloadFeaturesActor();
        List<Feature> features = new ArrayList<>();
        ConditionalInterface conditionalInterface1 = actor.configure(features);
        Feature processFeature = actor.nextFeature();
        fail();
    }
}

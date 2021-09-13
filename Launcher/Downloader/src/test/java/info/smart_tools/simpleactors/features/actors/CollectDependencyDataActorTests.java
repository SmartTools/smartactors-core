package info.smart_tools.simpleactors.features.actors;

import info.smart_tools.smartactors.downloader.commons.actors.ConditionalInterface;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.actors.CollectDependencyDataActor;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class CollectDependencyDataActorTests {

    @Test
    public void creating() {
        CollectDependencyDataActor actor = new CollectDependencyDataActor();
        assertNotNull(actor);
        Feature feature1 = new Feature();
        ConditionalInterface conditionalInterface = actor.configure(feature1);
        assertFalse(conditionalInterface.compare());
    }

    @Test
    public void processFeatureWithDependencies() {
        CollectDependencyDataActor actor = new CollectDependencyDataActor();
        Feature feature1 = new Feature();
        feature1.setAfterFeatures(Arrays.asList("a", "b"));
        feature1.setPlugins(Arrays.asList("c", "d"));
        ConditionalInterface conditionalInterface1 = actor.configure(feature1);
        assertTrue(conditionalInterface1.compare());
        Feature feature2 = new Feature();
        feature2.setAfterFeatures(Arrays.asList());
        feature2.setPlugins(Arrays.asList());
        ConditionalInterface conditionalInterface2 = actor.configure(feature2);
        assertFalse(conditionalInterface2.compare());
    }

    @Test
    public void checkProcessingNext() {
        CollectDependencyDataActor actor = new CollectDependencyDataActor();
        Feature feature1 = new Feature();
        feature1.setAfterFeatures(Arrays.asList("feature2"));
        ConditionalInterface conditionalInterface1 = actor.configure(feature1);
        assertTrue(conditionalInterface1.compare());
        Feature feature2 = actor.processNextFeature();
        assertEquals("feature2", feature2.getFeatureName());
    }

    @Test(expected = RuntimeException.class)
    public void checkProcessingNext_NegativeCase() {
        CollectDependencyDataActor actor = new CollectDependencyDataActor();
        Feature feature1 = new Feature();
        ConditionalInterface conditionalInterface1 = actor.configure(feature1);
        assertFalse(conditionalInterface1.compare());
        actor.processNextFeature();
        fail();
    }

    @Test
    public void checkAddingNewFeatures() {
        CollectDependencyDataActor actor = new CollectDependencyDataActor();
        Feature feature1 = new Feature("feature1");
        Feature feature2 = new Feature("feature2");
        feature2.setAfterFeatures(Collections.singletonList("feature3"));
        feature2.setPlugins(Collections.singletonList("feature4"));
        ConditionalInterface conditionalInterface1 = actor.configure(feature1);
        assertFalse(conditionalInterface1.compare());
        actor.addNewFeatures(feature2);
        assertTrue(conditionalInterface1.compare());
        assertEquals("feature3", actor.processNextFeature().getFeatureName());
        assertEquals("feature4", actor.processNextFeature().getFeatureName());
        assertFalse(conditionalInterface1.compare());
        Feature feature5 = new Feature("feature5");
        actor.addNewFeatures(feature5);
        assertFalse(conditionalInterface1.compare());
        Feature feature6 = new Feature("feature6");
        feature6.setAfterFeatures(Collections.emptyList());
        feature6.setPlugins(Collections.emptyList());
        actor.addNewFeatures(feature6);
        assertFalse(conditionalInterface1.compare());
        actor.addNewFeatures(null);
        assertFalse(conditionalInterface1.compare());
        Feature feature7 = new Feature("feature7");
        feature7.setAfterFeatures(Collections.singletonList("feature2"));
        feature7.setPlugins(Collections.singletonList("feature3"));
        actor.addNewFeatures(feature7);
        assertFalse(conditionalInterface1.compare());
    }
}

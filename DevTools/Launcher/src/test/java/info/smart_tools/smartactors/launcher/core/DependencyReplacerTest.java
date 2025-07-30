package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.feature.Feature;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyReplacer;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class DependencyReplacerTest {

    private IDependencyReplacer dependencyReplacer;

    @Before
    public void init() {
        this.dependencyReplacer = new DependencyReplacer();
    }

    @Test
    public void testReplaceDependencies() {
        IFeature feature1 = new Feature(
                UUID.randomUUID(),
                "feature1.jar",
                "feature1",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("feature1-plugin"))
        );
        IFeature feature1Plugin = new Feature(
                UUID.randomUUID(),
                "feature1-plugin.jar",
                "feature1-plugin",
                new ArrayList<>(Arrays.asList("feature1")),
                new ArrayList<>()
        );

        IFeature feature2 = new Feature(
                UUID.randomUUID(),
                "feature2.jar",
                "feature2",
                new ArrayList<>(Arrays.asList("feature1")),
                new ArrayList<>(Arrays.asList("feature2-plugin1", "feature2-plugin2"))
        );
        IFeature feature2Plugin1 = new Feature(
                UUID.randomUUID(),
                "feature2-plugin1.jar",
                "feature2-plugin1",
                new ArrayList<>(Arrays.asList("feature2")),
                new ArrayList<>()
        );
        IFeature feature2Plugin2 = new Feature(
                UUID.randomUUID(),
                "feature2-plugin2.jar",
                "feature2-plugin2",
                new ArrayList<>(Arrays.asList("feature1", "feature2")),
                new ArrayList<>()
        );

        List<IFeature> features = Arrays.asList(
                feature1,
                feature1Plugin,
                feature2,
                feature2Plugin1,
                feature2Plugin2
        );
        dependencyReplacer.replaceDependencies(features);

        assertEquals(new ArrayList<>(), feature1.getAfterFeatures());
        assertEquals(Arrays.asList("feature1"), feature1Plugin.getAfterFeatures());
        assertEquals(Arrays.asList("feature1-plugin"), feature2.getAfterFeatures());
        assertEquals(Arrays.asList("feature2"), feature2Plugin1.getAfterFeatures());
        assertEquals(Arrays.asList("feature2", "feature1-plugin"), feature2Plugin2.getAfterFeatures());
    }

    @Test
    public void testDoNotReplaceDependencyIfFeatureIsInPlugins() {
        IFeature feature1 = new Feature(
                UUID.randomUUID(),
                "feature1.jar",
                "feature1",
                new ArrayList<>(),
                new ArrayList<>(Arrays.asList("feature1-plugin"))
        );
        IFeature feature1Plugin = new Feature(
                UUID.randomUUID(),
                "feature1-plugin.jar",
                "feature1-plugin",
                new ArrayList<>(Arrays.asList("feature1")),
                new ArrayList<>()
        );
        IFeature feature2 = new Feature(
                UUID.randomUUID(),
                "feature2.jar",
                "feature2",
                new ArrayList<>(Arrays.asList("feature1")),
                new ArrayList<>(Arrays.asList("feature1"))
        );

        List<IFeature> features = Arrays.asList(
                feature1,
                feature1Plugin,
                feature2
        );
        dependencyReplacer.replaceDependencies(features);

        assertEquals(new ArrayList<>(), feature1.getAfterFeatures());
        assertEquals(Arrays.asList("feature1"), feature1Plugin.getAfterFeatures());
        assertEquals(Arrays.asList("feature1"), feature2.getAfterFeatures());
    }
}

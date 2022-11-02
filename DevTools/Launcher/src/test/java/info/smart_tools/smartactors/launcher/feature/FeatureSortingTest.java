package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureSortingException;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureSorting;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class FeatureSortingTest {

    private IFeatureSorting featureSorting;

    @Before
    public void init() {
        this.featureSorting = new FeatureSorting();
    }

    @Test
    public void testSortFeatures() throws Exception {
        List<IFeature> features = new ArrayList<>();
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 2 path",
                "feature 2",
                Arrays.asList("feature 1"),
                new ArrayList<>()
        ));
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 4 path",
                "feature 4",
                Arrays.asList("feature 2"),
                new ArrayList<>()
        ));
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 1 path",
                "feature 1",
                new ArrayList<>(),
                new ArrayList<>()
        ));
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 3 path",
                "feature 3",
                Arrays.asList("feature 1", "feature 2"),
                new ArrayList<>()
        ));

        List<IFeature> sortedFeatures = featureSorting.sortFeatures(features);

        assertEquals("feature 1", sortedFeatures.get(0).getName());
        assertEquals("feature 2", sortedFeatures.get(1).getName());
        assertEquals("feature 4", sortedFeatures.get(2).getName());
        assertEquals("feature 3", sortedFeatures.get(3).getName());
    }

    @Test(expected = FeatureSortingException.class)
    public void testSortFeaturesCyclicalDependency() throws Exception {
        List<IFeature> features = new ArrayList<>();
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 2 path",
                "feature 2",
                Arrays.asList("feature 1"),
                new ArrayList<>()
        ));
        features.add(new Feature(
                UUID.randomUUID(),
                "feature 1 path",
                "feature 1",
                Arrays.asList("feature 2"),
                new ArrayList<>()
        ));

        featureSorting.sortFeatures(features);
    }
}

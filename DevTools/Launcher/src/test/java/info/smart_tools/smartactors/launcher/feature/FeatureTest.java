package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.path.Path;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeatureTest {

    @Test
    public void testFeatureModel() {
        List<String> dependencies = new ArrayList<>();
        List<String> plugins = new ArrayList<>();

        IFeature feature = new Feature(
                "id",
                "file name",
                "feature",
                dependencies,
                plugins
        );

        assertEquals("id", feature.getId());
        assertEquals("file name", feature.getFileName());
        assertEquals("feature", feature.getName());

        IPath path = new Path("file name");
        assertEquals(path, feature.getPath());
        assertEquals(new ArrayList<>(), feature.getAfterFeatures());
        assertEquals(new ArrayList<>(), feature.getPlugins());

        List<String> newDependencies = Arrays.asList("feature1", "feature2");
        feature.setAfterFeatures(newDependencies);
        assertEquals(newDependencies, feature.getAfterFeatures());
    }
}

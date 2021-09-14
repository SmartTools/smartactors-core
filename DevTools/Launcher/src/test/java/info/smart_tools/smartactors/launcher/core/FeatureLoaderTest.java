package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IFeatureLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.FeatureLoaderException;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;

public class FeatureLoaderTest {

    private IFeatureLoader featureLoader;

    @Before
    public void init() {
        this.featureLoader = new FeatureLoader();
    }

    @Test
    public void testLoadFeatures() throws Exception {
        String featuresPath = getClass().getClassLoader().getResource("features").getPath();
        List<IPath> paths = featureLoader.loadFeatures(featuresPath);

        assertTrue(paths.stream().anyMatch(it -> it.getPath().contains("features/addon/core-service-starter-0.7.0.jar")));
        assertTrue(paths.stream().anyMatch(it -> it.getPath().contains("features/simple-feature-0.7.0.jar")));
        assertTrue(paths.stream().anyMatch(it -> it.getPath().contains("features/simple-feature-plugin-0.7.0.jar")));
    }

    @Test(expected = FeatureLoaderException.class)
    public void testLoadFeaturesPathIsNull() throws Exception {
        featureLoader.loadFeatures(null);
    }

    @Test(expected = FeatureLoaderException.class)
    public void testLoadFeaturesEmptyPath() throws Exception {
        featureLoader.loadFeatures("");
    }

    @Test(expected = FeatureLoaderException.class)
    public void testLoadFeaturesNonExistingPath() throws Exception {
        featureLoader.loadFeatures("sample");
    }
}

package info.smart_tools.smartactors.downloader.features;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

public class FeatureNamespaceTests {

    @Test
    public void creatingAndParsing() throws Exception {
        FeatureNamespace featureNamespace = new FeatureNamespace("test1:test2:test3");
        assertNotNull(featureNamespace);
        assertEquals("test1", featureNamespace.getGroup());
        assertEquals("test2", featureNamespace.getName());
        assertEquals("test3", featureNamespace.getVersion());
    }

    @Test(expected = Exception.class)
    public void exceptionOnParsingFirstCase() throws Exception {
        FeatureNamespace featureNamespace = new FeatureNamespace(null);
        fail();
    }

    @Test(expected = Exception.class)
    public void exceptionOnParsingSecondCase() throws Exception {
        FeatureNamespace featureNamespace = new FeatureNamespace("test");
        fail();
    }
}

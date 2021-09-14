package info.smart_tools.smartactors.downloader.features;

import info.smart_tools.smartactors.downloader.Repository;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class FeatureListTests {

    @Test
    public void creationAndModification() {
        FeatureList featureList = new FeatureList();
        List<FeatureNamespace> featureNamespaces = new ArrayList<>();
        List<Repository> repositories = new ArrayList<>();
        featureList.setFeatures(featureNamespaces);
        featureList.setRepositories(repositories);

        assertEquals(featureNamespaces, featureList.getFeatures());
        assertEquals(repositories, featureList.getRepositories());
    }
}

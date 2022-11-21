//package info.smart_tools.smartactors.downloader.feature_downloader;
//
//import info.smart_tools.smartactors.downloader.Repository;
//import info.smart_tools.smartactors.downloader.features.FeatureNamespace;
//import org.jboss.shrinkwrap.resolver.api.maven.ConfigurableMavenResolverSystem;
//import org.jboss.shrinkwrap.resolver.api.maven.MavenFormatStage;
//import org.jboss.shrinkwrap.resolver.api.maven.MavenStrategyStage;
//import org.junit.Test;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNotNull;
//import static org.junit.Assert.assertNull;
//import static org.mockito.Matchers.any;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.times;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.when;
//
//public class MavenFeatureDownloaderTests {
//
//    private final static String URL_DELIMITER = "/";
//    private final static String MAVEN_REPOSITORY_LAYOUT = "default";
//    private final static String FEATURE_NAMESPACE_DELIMITER = ":";
//
//    @Test
//    public void creatingAndUsing() throws Exception {
//        ShrinkwrapResolver downloader = new ShrinkwrapResolver();
//        Repository repository1 = new Repository();
//        assertNotNull(downloader);
//        ConfigurableMavenResolverSystem resolver = mock(ConfigurableMavenResolverSystem.class);
//        downloader.initialize(resolver);
//
//        downloader.addRepository(repository1);
//        verify(resolver, times(1))
//                .withRemoteRepo(
//                        repository1.getId(),
//                        repository1.getUrl() + URL_DELIMITER + repository1.getId(),
//                        MAVEN_REPOSITORY_LAYOUT
//                );
//        List<Repository> repositoryList = new ArrayList<>();
//        Repository repository2 = new Repository();
//        repositoryList.add(repository1);
//        repositoryList.add(repository2);
//
//        downloader.addRepositories(repositoryList);
//        verify(resolver, times(3))
//                .withRemoteRepo(
//                        any(String.class),
//                        any(String.class),
//                        any(String.class)
//                );
//
//        MavenStrategyStage mss = mock(MavenStrategyStage.class);
//        MavenFormatStage mfs = mock(MavenFormatStage.class);
//        File f1 = new File("1");
//        File f2 = new File("2");
//        File[] files = new File[]{f1, f2};
//        when(resolver.resolve(any(String.class))).thenReturn(mss);
//        when(mss.withoutTransitivity()).thenReturn(mfs);
//        when(mfs.asFile()).thenReturn(files);
//        FeatureNamespace featureNamespace = new FeatureNamespace("group:artifact:version");
//        List<File> result = downloader.download(featureNamespace, "json");
//        verify(resolver, times(1))
//                .resolve("group:artifact:json:version");
//        assertEquals(2, result.size());
//        assertEquals(f1, result.get(0));
//        assertEquals(f2, result.get(1));
//
//        result = downloader.download(null, "json");
//        assertNull(result);
//
//        result = downloader.download(featureNamespace, null);
//        assertNull(result);
//
//        result = downloader.download(null, null);
//        assertNull(result);
//    }
//}

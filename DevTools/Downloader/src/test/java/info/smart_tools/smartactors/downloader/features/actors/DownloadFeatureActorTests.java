package info.smart_tools.smartactors.downloader.features.actors;

import info.smart_tools.simpleactors.commons.CommonParameters;
import info.smart_tools.simpleactors.commons.IMessage;
import info.smart_tools.simpleactors.commons.Message;
import info.smart_tools.simpleactors.commons.MethodParameters;
import info.smart_tools.smartactors.downloader.Params__DownloadFeature;
import info.smart_tools.smartactors.downloader.Repository;
import info.smart_tools.smartactors.downloader.feature_downloader.IFeatureDownloader;
import info.smart_tools.smartactors.downloader.features.Feature;
import info.smart_tools.smartactors.downloader.features.FeatureNamespace;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.junit.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DownloadFeatureActorTests {

    @Test
    public void creating() {
        IFeatureDownloader downloader = mock(IFeatureDownloader.class);
        DownloadFeatureActor actor = new DownloadFeatureActor(downloader);

        assertNotNull(actor);
    }

    @Test
    public void downloadFeaturePositiveCase()
            throws Exception {
        IFeatureDownloader downloader = mock(IFeatureDownloader.class);
        List<File> files = new ArrayList<>();
        File responseFile = new File("resultFile");
        files.add(responseFile);
        Feature feature = mock(Feature.class);
        String featureNamespace = "group:artifact:1";
        String featureType = "jar";
        List<Repository> repositories = new ArrayList<>();
        IMessage message = Message.builder().build();
        File dir = new File("test");

        String featureParamName = "feature";
        String featureTypeParamName = "featureType";
        String directoryParamName = "directory";

        List<String> argumentPaths = Arrays.asList(
                featureParamName, Params__DownloadFeature.REPOSITORIES, featureTypeParamName, directoryParamName
        );
        MethodParameters parameters = new MethodParameters(argumentPaths, CommonParameters.SPLIT_RESPONSE);

        message.put(featureParamName, feature);
        message.put(featureTypeParamName, featureType);
        message.put(Params__DownloadFeature.REPOSITORIES, repositories);
        message.put(directoryParamName, dir);
        message.put(CommonParameters.METHOD_PARAMS, parameters);

        when(feature.getFeatureName()).thenReturn(featureNamespace);
        when(downloader.download(any(FeatureNamespace.class), eq(featureType))).thenReturn(files);

        DownloadFeatureActor actor = new DownloadFeatureActor(downloader);
        actor.execute("downloadFeature", message);

//        verify(downloader, times(1)).initialize(null);
        verify(downloader, times(1)).addRepositories(repositories);
        verify(downloader, times(1)).download(any(FeatureNamespace.class), eq(featureType));

        File resultFile = message.get(Params__DownloadFeature.DOWNLOADED_FILE);
        assertEquals(responseFile, resultFile);
        Path path1 = message.get(Params__DownloadFeature.CHECK_AND_DELETE_IF_EXIST_FILE_PATH);
        assertEquals(Paths.get(dir.toString(), responseFile.toString()), path1);
        Path path2 = message.get(Params__DownloadFeature.NEW_FILE_FOR_FEATURE_PATH);
        assertEquals(Paths.get(dir.toString(), responseFile.toString()), path2);
        Path path3 = message.get(Params__DownloadFeature.DOWNLOADED_FILE_PATH);
        assertEquals(Paths.get(responseFile.toString()), path3);
    }

    @Test(expected = RuntimeException.class)
    public void downloadFeatureNegativeCase()
            throws Exception {
        IFeatureDownloader downloader = mock(IFeatureDownloader.class);
        DownloadFeatureActor actor = new DownloadFeatureActor(downloader);
        actor.downloadFeature(null, null, "", null);
        fail();
    }
}

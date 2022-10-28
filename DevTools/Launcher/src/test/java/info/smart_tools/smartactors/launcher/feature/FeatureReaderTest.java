package info.smart_tools.smartactors.launcher.feature;

import info.smart_tools.smartactors.launcher.interfaces.IObjectMapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.exception.ifeature.FeatureReaderException;
import info.smart_tools.smartactors.launcher.interfaces.exception.iobject_mapper.ReadJsonException;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.launcher.interfaces.ifeature.IFeatureReader;
import info.smart_tools.smartactors.launcher.interfaces.ilogger.ILogger;
import info.smart_tools.smartactors.launcher.model.FeatureConfig;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.stubbing.Answer;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Ignore("missing files for this test")
public class FeatureReaderTest {

    private IFeatureReader featureReader;

    private ILogger log;
    private IObjectMapper objectMapper;

    @Before
    public void init() {
        this.log = mock(ILogger.class);
        Answer<?> logMock = a -> {
            String message = (String) a.getArguments()[0];
            String format = (String) a.getArguments()[1];
            System.out.println(MessageFormat.format(message, format));
            return null;
        };
        doAnswer(logMock).when(log).debug(anyString(), anyString());
        doAnswer(logMock).when(log).info(anyString(), anyString());
        doAnswer(logMock).when(log).warning(anyString(), anyString());
        doAnswer(logMock).when(log).error(anyString(), anyString());

        this.objectMapper = mock(IObjectMapper.class);
        this.featureReader = new FeatureReader(log, objectMapper);
    }

    @Test
    public void testReadFeatures() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        doReturn(new FeatureConfig("simple feature", new ArrayList<>(), new ArrayList<>()))
                .when(objectMapper)
                .readJson(anyString(), any(Class.class));

        List<IFeature> features = this.featureReader.readFeatures(paths);

        assertFalse(features.isEmpty());
    }

    @Test
    public void testReadFeaturesCatchReadJsonException() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        doThrow(new ReadJsonException("exception")).when(objectMapper).readJson(anyString(), any(Class.class));

        this.featureReader.readFeatures(paths);
        verify(log, times(paths.size())).error(anyString(), anyString());
    }

    @Test(expected = FeatureReaderException.class)
    public void testReadFeaturesGeneralException() throws Exception {
        String pathToFeature = getClass().getClassLoader().getResource("features/simple-feature-0.7.0.jar").getPath();
        String pathToPlugin = getClass().getClassLoader().getResource("features/simple-feature-plugin-0.7.0.jar").getPath();

        IPath path1 = mock(IPath.class);
        IPath path2 = mock(IPath.class);
        when(path1.getPath()).thenReturn(pathToFeature);
        when(path2.getPath()).thenReturn(pathToPlugin);
        List<IPath> paths = Arrays.asList(path1, path2);

        doReturn(null).when(objectMapper).readJson(anyString(), any(Class.class));

        this.featureReader.readFeatures(paths);
    }
}

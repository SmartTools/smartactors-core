package info.smart_tools.smartactors.launcher.path;

import info.smart_tools.smartactors.launcher.interfaces.IPath;
import org.junit.Test;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

public class PathTest {

    @Test
    public void testPathFromString() {
        IPath path = new Path("path");
        assertEquals("path", path.getPath());
    }

    @Test
    public void testPathFromFile() {
        URL featureUrl = getClass().getClassLoader().getResource("features" + File.separator + "simple-feature-0.7.0.jar");
        File file = new File(featureUrl.getFile());
        IPath path = new Path(file);
        assertEquals(featureUrl.getPath(), path.getPath());
    }

    @Test
    public void testPathFromNioPath() throws Exception {
        URL featureUrl = getClass().getClassLoader().getResource("features" + File.separator + "simple-feature-0.7.0.jar");
        java.nio.file.Path nioPath = Paths.get(featureUrl.toURI());
        IPath path = new Path(nioPath);
        assertEquals(featureUrl.getPath(), path.getPath());
    }

    @Test
    public void Should_beEqualForTheSamePath() {
        IPath path1 = new Path("foo" + File.separator + "bar");
        IPath path2 = new Path("foo" + File.separator + "bar");
        assertEquals(path1, path2);
    }
}

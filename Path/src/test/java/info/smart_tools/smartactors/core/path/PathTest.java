package info.smart_tools.smartactors.core.path;

import info.smart_tools.smartactors.core.ipath.IPath;
import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;

import static org.junit.Assert.assertEquals;

/**
 * Tests for Path implementation
 */
public class PathTest {

    @Test
    public void Should_beCreatedFromString() {
        IPath path = new Path("foo/bar");
        assertEquals("foo/bar", path.getPath());
    }

    @Test
    public void Should_beCreatedFromFile() {
        File file = new File("foo/bar");
        IPath path = new Path(file);
        assertEquals("foo/bar", path.getPath());
    }

    @Test
    public void Should_beCreatedFromNioPath() {
        java.nio.file.Path nioPath = Paths.get("foo", "bar");
        IPath path = new Path(nioPath);
        assertEquals("foo/bar", path.getPath());
    }

    @Test
    public void Should_beEqualForTheSamePath() {
        IPath path1 = new Path("foo/bar");
        IPath path2 = new Path("foo/bar");
        assertEquals(path1, path2);
    }

}

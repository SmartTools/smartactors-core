package info.smart_tools.smartactors.core.plugin_loader_from_jar;

import com.sun.org.apache.bcel.internal.util.ClassLoader;
import info.smart_tools.smartactors.core.path.Path;
import info.smart_tools.smartactors.core.ipath.IPath;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iplugin_loader.IPluginLoader;
import info.smart_tools.smartactors.core.iplugin_loader.exception.PluginLoaderException;
import info.smart_tools.smartactors.core.iplugin_loader_visitor.IPluginLoaderVisitor;
import org.junit.Test;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;

/**
 * Tests for PluginLoader
 */
public class PluginLoaderTest {

    @Test
    public void checkPluginLoaderCreation()
            throws Exception {
        Checker checker = new Checker();
        ExpansibleURLClassLoader cl = new ExpansibleURLClassLoader(new URL[]{});
        IPluginLoaderVisitor<String> visitor = mock(IPluginLoaderVisitor.class);
        IPluginLoader<Collection<IPath>> pl = new PluginLoader(
                cl,
                (t) -> {
                    try {
                        checker.wasCalled = true;
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create instance of IPlugin");
                    }
                },
                visitor);
        URL url = this.getClass().getClassLoader().getResource("test_jar_package.jar");
        if (null == url) {
            fail();
        }
        Collection<IPath> files = new ArrayList<IPath>(){{add(new Path(url.getPath()));}};
        pl.loadPlugin(files);
        assertTrue(checker.wasCalled);
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullArgs()
            throws Exception {
        new PluginLoader(null, null, null);
        fail();
    }

    @Test (expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnInvalidClassLoader()
            throws Exception {
        new PluginLoader(new ClassLoader(), (t)->{}, mock(IPluginLoaderVisitor.class));
        fail();
    }

    @Test (expected = PluginLoaderException.class)
    public void checkPluginLoaderException()
            throws Exception {
        ExpansibleURLClassLoader cl = new ExpansibleURLClassLoader(new URL[]{});
        IPluginLoaderVisitor<String> visitor = mock(IPluginLoaderVisitor.class);
        IPluginLoader<Collection<IPath>> pl = new PluginLoader(
                cl,
                (t) -> {
                    throw new RuntimeException("Could not create instance of IPlugin");
                },
                visitor);
        pl.loadPlugin(null);
        fail();
    }

    @Test (expected = PluginLoaderException.class)
    public void checkPluginLoaderOnLoadBrokenJarFile()
            throws Exception {
        Checker checker = new Checker();
        ExpansibleURLClassLoader cl = new ExpansibleURLClassLoader(new URL[]{});
        IPluginLoaderVisitor<String> visitor = mock(IPluginLoaderVisitor.class);
        IPluginLoader<Collection<IPath>> pl = new PluginLoader(
                cl,
                (t) -> {
                    try {
                        checker.wasCalled = true;
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create instance of IPlugin");
                    }
                },
                visitor);
        URL url = this.getClass().getClassLoader().getResource("broken.jar");
        if (null == url) {
            fail();
        }
        Collection<IPath> files = new ArrayList<IPath>(){{add(new Path(url.getPath()));}};
        pl.loadPlugin(files);
        assertTrue(checker.wasCalled);
        fail();
    }

    @Test (expected = PluginLoaderException.class)
    public void checkPluginLoaderOnLoadJarWithBrokenClassFile()
            throws Exception {
        Checker checker = new Checker();
        ExpansibleURLClassLoader cl = new ExpansibleURLClassLoader(new URL[]{});
        IPluginLoaderVisitor<String> visitor = mock(IPluginLoaderVisitor.class);
        IPluginLoader<Collection<IPath>> pl = new PluginLoader(
                cl,
                (t) -> {
                    try {
                        checker.wasCalled = true;
                    } catch (Exception e) {
                        throw new RuntimeException("Could not create instance of IPlugin");
                    }
                },
                visitor);
        URL url = this.getClass().getClassLoader().getResource("test_jar_package_with_broken_class.jar");
        if (null == url) {
            fail();
        }
        Collection<IPath> files = new ArrayList<IPath>(){{add(new info.smart_tools.smartactors.core.path.Path(url.getPath()));}};
        pl.loadPlugin(files);
        assertTrue(checker.wasCalled);
        fail();
    }
}

class Checker {
    public boolean wasCalled = false;
}

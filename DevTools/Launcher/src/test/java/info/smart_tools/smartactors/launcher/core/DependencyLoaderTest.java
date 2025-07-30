package info.smart_tools.smartactors.launcher.core;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.IPath;
import info.smart_tools.smartactors.launcher.interfaces.core.IDependencyLoader;
import info.smart_tools.smartactors.launcher.interfaces.exception.core.DependencyLoaderException;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DependencyLoaderTest {

    private IDependencyLoader dependencyLoader;

    @Before
    public void init() {
        this.dependencyLoader = new DependencyLoader();
    }

    @Test
    public void testLoadingDependency() throws DependencyLoaderException {
        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);
        IPath path = mock(IPath.class);
        when(path.getPath()).thenReturn("path");
        doAnswer((invocation) -> {
            System.out.println("[TEST] Added url '" + invocation.getArguments()[0] +"' to class loader");
            return null;
        }).when(classLoader).addURL(any(URL.class));

        List<IPath> paths = Arrays.asList(path);

        dependencyLoader.load(classLoader, paths);

        verify(classLoader, times(1)).addURL(any(URL.class));
    }
}

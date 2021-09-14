package info.smart_tools.smartactors.launcher.classloader;

import info.smart_tools.smartactors.class_management.interfaces.ismartactors_class_loader.ISmartactorsClassLoader;
import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import org.junit.Before;
import org.junit.Test;

import java.net.URL;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class SmartactorsClassLoaderWrapperTest {

    private ISmartactorsClassLoader smartactorsClassLoader;

    private IClassLoaderWrapper classLoader;

    @Before
    public void init() {
        this.smartactorsClassLoader = mock(ISmartactorsClassLoader.class);
        this.classLoader = new SmartactorsClassLoaderWrapper(smartactorsClassLoader);
    }

    @Test
    public void testAddUrl() throws Exception {
        AtomicReference<Boolean> urlAdded = new AtomicReference<>(false);
        doAnswer(a -> {
            urlAdded.set(true);
            return null;
        }).when(smartactorsClassLoader).addURL(any());

        classLoader.addURL(new URL("jar:file:sample!/"));

        assertTrue(urlAdded.get());
    }

    @Test
    public void testLoadClass() throws Exception {
        Class<Object> clazz = Object.class;
        doReturn(clazz).when(smartactorsClassLoader).loadClass(any());

        Class<Object> loadedClass = (Class<Object>) classLoader.loadClass("class name");

        assertEquals(clazz, loadedClass);
    }

    @Test(expected = ClassNotFoundException.class)
    public void testExceptionLoadClass() throws Exception {
        doThrow(new ClassNotFoundException("No class")).when(smartactorsClassLoader).loadClass(any());

        classLoader.loadClass("class name");
    }
}

package info.smart_tools.smartactors.core.plugin_loader_from_jar;

import org.junit.Test;

import java.net.URL;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

/**
 * Tests for ExpansibleURLClassLoader
 */
public class ExpansibleURLClassLoaderTest {

    @Test
    public void checkExpansibleURLClassLoaderCreation() {
        ExpansibleURLClassLoader cl1 = new ExpansibleURLClassLoader(new URL[]{});
        assertNotNull(cl1);
        ExpansibleURLClassLoader cl2 = new ExpansibleURLClassLoader(new URL[]{}, ClassLoader.getSystemClassLoader());
        assertNotNull(cl2);
        assertSame(cl2.getParent(), ClassLoader.getSystemClassLoader());
    }

    @Test
    public void checkAdditionNewURL()
            throws Exception {
        URL url1 = new URL("http", "host", 9000, "filename1");
        URL url2 =  new URL("http", "host", 9000, "filename2");
        URL url3 =  new URL("http", "host", 9000, "filename1");
        ExpansibleURLClassLoader cl = new ExpansibleURLClassLoader(new URL[]{url1});
        assertNotNull(cl);
        cl.addUrl(url2);
        cl.addUrl(url3);
        URL[] result = cl.getURLs();
        assertSame(result[0], url1);
        assertSame(result[1], url2);
        assertEquals(result.length, 2);
    }
}

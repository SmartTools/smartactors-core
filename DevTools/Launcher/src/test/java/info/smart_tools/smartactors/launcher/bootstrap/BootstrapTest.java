package info.smart_tools.smartactors.launcher.bootstrap;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

public class BootstrapTest {

    private Class<?> bootstrapCls;

    private IBootstrap bootstrap;

    @Before
    public void init() throws Exception {
        this.bootstrapCls = BootstrapSample.class;
        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);

        doReturn(bootstrapCls).when(classLoader).loadClass(any());
        this.bootstrap = new Bootstrap(classLoader);
    }

    @Test
    public void testBootstrapStart() throws Exception {
        bootstrap.start();
        assertTrue((Boolean) bootstrapCls.getMethod("getBootstrapStarted").invoke(bootstrap.getInstance()));
    }

    @Test
    public void testBootstrapRevert() throws Exception {
        bootstrap.revert();
        assertTrue((Boolean) bootstrapCls.getMethod("getBootstrapReverted").invoke(bootstrap.getInstance()));
    }

    @Test
    public void testGetBootstrapInstance() {
        assertNotNull(bootstrap.getInstance());
    }
}

class BootstrapSample {
    private Boolean bootstrapStarted;
    private Boolean bootstrapReverted;

    public BootstrapSample() {
        this.bootstrapStarted = false;
        this.bootstrapReverted = false;
    }

    public void start() {
        this.bootstrapStarted = true;
    }

    public void revert() {
        this.bootstrapReverted = true;
    }

    public Boolean getBootstrapStarted() {
        return bootstrapStarted;
    }

    public Boolean getBootstrapReverted() {
        return bootstrapReverted;
    }
}

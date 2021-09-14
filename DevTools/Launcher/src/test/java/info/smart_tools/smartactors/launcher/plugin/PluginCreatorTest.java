package info.smart_tools.smartactors.launcher.plugin;

import info.smart_tools.smartactors.launcher.interfaces.IClassLoaderWrapper;
import info.smart_tools.smartactors.launcher.interfaces.exception.iplugin.PluginCreationException;
import info.smart_tools.smartactors.launcher.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.launcher.interfaces.iplugin.IPluginCreator;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class PluginCreatorTest {

    private IPluginCreator pluginCreator;

    @Before
    public void init() throws Exception {
        IClassLoaderWrapper classLoader = mock(IClassLoaderWrapper.class);
        doReturn(PluginCreatorInstance.class)
                .when(classLoader).loadClass("info.smart_tools.smartactors.feature_loading_system.plugin_creator.PluginCreator");
        doReturn(Object.class)
                .when(classLoader).loadClass("info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap");

        this.pluginCreator = new PluginCreator(classLoader);
    }

    @Test
    public void testCreatePlugin() throws Exception {
        IBootstrap bootstrap = mock(IBootstrap.class);

        Boolean result = (Boolean) pluginCreator.create(Object.class, bootstrap);

        assertTrue(result);
    }

    @Test(expected = PluginCreationException.class)
    public void testCreatePluginException() throws Exception {
        IBootstrap bootstrap = mock(IBootstrap.class);
        when(bootstrap.getInstance()).thenThrow(new RuntimeException("Exception during getting instance"));

        pluginCreator.create(Object.class, bootstrap);
    }
}

class PluginCreatorInstance {
    public PluginCreatorInstance() {

    }

    public Boolean create(Class<?> clazz, Object bootstrap) {
        return true;
    }
}

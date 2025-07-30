package info.smart_tools.smartactors.feature_loading_system.plugin_creator;

import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin_creator.exception.PluginCreationException;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests for PluginCreator
 */
public class PluginCreatorTest {

    @Test
    public void checkPluginCreation()
            throws Exception {
        PluginCreator creator = new PluginCreator();
        IBootstrap<Object> bootstrap = mock(IBootstrap.class);
        doNothing().when(bootstrap).add(bootstrap);
        IPlugin plugin = creator.create(PluginTest.class, bootstrap);
        assertNotNull(plugin);
        plugin.load();
        verify(bootstrap, times(1)).add(any(Object.class));
    }

    @Test (expected = PluginCreationException.class)
    public void checkPluginCreationException()
            throws Exception {
        PluginCreator creator = new PluginCreator();
        IBootstrap<Object> bootstrap = mock(IBootstrap.class);
        creator.create(Object.class, bootstrap);
        fail();
    }
}

class PluginTest implements IPlugin {

    IBootstrap<Object> bootstrap;

    public PluginTest(IBootstrap<Object> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        bootstrap.add(new Object());
    }
}
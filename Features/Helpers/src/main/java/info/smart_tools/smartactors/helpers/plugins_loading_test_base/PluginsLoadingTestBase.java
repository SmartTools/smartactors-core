package info.smart_tools.smartactors.helpers.plugins_loading_test_base;

import info.smart_tools.smartactors.feature_loading_system.bootstrap.Bootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import org.junit.Before;

/**
 * Base class for tests that require some plugins (e.g. IOC, Scopes, Keys) to be loaded as precondition.
 */
public abstract class PluginsLoadingTestBase {
    private Bootstrap bootstrap;

    /**
     * @throws Exception if any error occurs
     */
    @Before
    public void setUpPlugins()
            throws Exception {
        bootstrap = new Bootstrap();
        loadPlugins();
        bootstrap.start();
        registerMocks();
    }

    /**
     * Load specified plugin.
     *
     * @param pluginClass    the plugin class to load
     * @throws Exception if any error occurs
     */
    protected void load(final Class<? extends IPlugin> pluginClass)
            throws Exception {
        pluginClass.getConstructor(IBootstrap.class).newInstance(bootstrap).load();
    }

    /**
     * Load plugins required for a test.
     *
     * @throws Exception if any error occurs
     */
    protected abstract void loadPlugins() throws Exception;

    /**
     * Called after plugins are loaded.
     *
     * @throws Exception if any error occurs
     */
    protected void registerMocks() throws Exception {}
}

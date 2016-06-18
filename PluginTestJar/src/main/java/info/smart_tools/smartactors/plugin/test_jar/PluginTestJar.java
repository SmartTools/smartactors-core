package info.smart_tools.smartactors.plugin.test_jar;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;

/**
 * Test Plugin.
 * Needed for run tests for IPluginLoader
 */
public class PluginTestJar implements IPlugin {

    /**
     * Inner container for incoming instance of {@link IBootstrap}
     */
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor.
     * It will be finding by PluginLoader
     * @param bootstrap instance of {@link IBootstrap}
     */
    public PluginTestJar(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load()
            throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("test");
            item.process(()-> {
                System.out.print("Test plugin loaded.");
            });
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Could not load plugin", e);
        }
    }
}

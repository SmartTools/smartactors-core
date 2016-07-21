package info.smart_tools.smartactors.core.examples.feature;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;

/**
 *  Example of plugin.
 */
public class Plugin2 implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    public Plugin2(final IBootstrap<IBootstrapItem<String>> bootstrap) {   // this constructor signature is required
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("Plugin2");
            item.process(() -> System.out.println("Plugin2 initialized"));
            bootstrap.add(item);
        } catch (Exception e) {
            throw new PluginException("Could not load Plugin2", e);
        }
        System.out.println("Plugin2 loaded");
    }

}

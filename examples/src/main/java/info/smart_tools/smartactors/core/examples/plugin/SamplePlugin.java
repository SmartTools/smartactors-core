package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.examples.SampleClass;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 * Example of plugin.
 */
public class SamplePlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;                 // to register our BootstrapItem

    /**
     * Creates the plugin.
     * This constructor is called by {@link info.smart_tools.smartactors.core.plugin_loader_from_jar.PluginLoader},
     * so it must have one argument of type IBootstrap.
     * @param bootstrap bootstrap where this plugin puts {@link IBootstrapItem}.
     */
    public SamplePlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {   // this constructor signature is required
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("SamplePlugin");    // our item name
            item.after("IOC");                                                  // dependency, we need IOC
            item.process(() -> {
                try {
                    IKey key = Keys.getOrAdd("new SampleClass");
                    IOC.register(
                            key,
                            // it's the initialization action of our plugin
                            new CreateNewInstanceStrategy(
                                    (args) -> new SampleClass((String) args[0])
                            )
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("SamplePlugin initialized");
            });
            bootstrap.add(item);                                            // tell server about our intents
        } catch (Exception e) {
            throw new PluginException("Could not load SamplePlugin", e);
        }
        System.out.println("SamplePlugin loaded");
    }

}

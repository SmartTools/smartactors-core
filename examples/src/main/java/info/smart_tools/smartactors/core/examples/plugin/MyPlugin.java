package info.smart_tools.smartactors.core.examples.plugin;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.examples.MyClass;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;

/**
 *  Example of plugin.
 */
public class MyPlugin implements IPlugin {

    private final IBootstrap<IBootstrapItem<String>> bootstrap;             // to register our BootstrapItem

    public MyPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {   // this constructor signature is required
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("MyPlugin");    // our item name
            item.after("IOC");                                              // dependency, we need IOC
            item.process(() -> {
                try {
                    IKey key = IOC.resolve(IOC.getKeyForKeyStorage(), "new MyClass");
                    IOC.register(
                            key,
                            // it's the initialization action of our plugin
                            new CreateNewInstanceStrategy(
                                    (args) -> new MyClass((String) args[0])
                            )
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println("MyPlugin initialized");
            });
            bootstrap.add(item);                                            // tell server about our intents
        } catch (Exception e) {
            throw new PluginException("Could not load MyPlugin", e);
        }
        System.out.println("MyPlugin loaded");
    }

}

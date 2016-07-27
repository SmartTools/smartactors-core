package info.smart_tools.smartactors.plugin.iobject_simple_impl;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject_simple_implementation.IObjectImpl;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 *
 */
public class IObjectSimpleImplPlugin implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * Constructor
     * @param bootstrap bootstrap element
     */
    public IObjectSimpleImplPlugin(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }


    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> item = new BootstrapItem("IObjectSimpleImplPlugin");
            item
                .after("IOC")
                .process(() -> {
                    try {
                        IKey fieldKey = Keys.getOrAdd(IObjectImpl.class.getCanonicalName());
                        IOC.register(fieldKey, new CreateNewInstanceStrategy(
                                (args) -> new IObjectImpl()
                        ));
                    } catch (RegistrationException | InvalidArgumentException | ResolutionException e) {
                        throw new RuntimeException(e);
                    }
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load IObject plugin", e);
        }
    }
}

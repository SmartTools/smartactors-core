package info.smart_tools.smartactors.iobject_plugins.dsobject_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class PluginDSObject implements IPlugin {
    private final IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     * @param bootstrap    the bootstrap
     */
    public PluginDSObject(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> dsObjectItem = new BootstrapItem("iobject");

            dsObjectItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                                    new CreateNewInstanceStrategy(args -> {
                                        if (args.length == 0) {
                                            return new DSObject();
                                        } else if (args.length == 1 && args[0] instanceof String) {
                                            try {
                                                return new DSObject((String) args[0]);
                                            } catch (InvalidArgumentException e) {
                                                throw new RuntimeException(e);
                                            }
                                        } else {
                                            throw new RuntimeException("Invalid arguments for IObject creation.");
                                        }
                                    }));
                        } catch (ResolutionException e) {
                            throw new ActionExecutionException("Dsobject plugin can't load: can't get Dsobject key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecutionException("Dsobject plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("Dsobject plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        String[] keyNames = { "info.smart_tools.smartactors.iobject.iobject.IObject" };
                        Keys.unregisterByNames(keyNames);
                    });

            bootstrap.add(dsObjectItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

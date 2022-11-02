package info.smart_tools.smartactors.ioc_plugins.ioc_keys_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.ioc.exception.DeletionException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_strategy.ResolveByNameIocStrategy;

/**
 *
 */
public class PluginIOCKeys implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public PluginIOCKeys(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            IBootstrapItem<String> iocKeysItem = new BootstrapItem("ioc_keys");

            iocKeysItem
                    .after("ioc_container")
                    .before("IOC")
                    .process(() -> {
                        try {
                            IOC.register(IOC.getKeyForKeyByNameStrategy(), new ResolveByNameIocStrategy());
                        } catch (RegistrationException e) {
                            throw new ActionExecutionException("IOCKeys plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            IOC.unregister(IOC.getKeyForKeyByNameStrategy());
                        } catch(DeletionException e) {
                            System.out.println("[WARNING] Deregistration of IOC key for key by name strategy failed.");
                        }
                    });

            bootstrap.add(iocKeysItem);
/*
            IBootstrapItem<String> iocDeregistrationsItem = new BootstrapItem("ioc_deregistrations");

            iocDeregistrationsItem
                    .after("ioc_keys")
                    .before("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getKeyByName("ioc unregister by names for bootstrap"),
                                    new ApplyFunctionToArgumentsStrategy(
                                            (args) -> {
                                                for(Object arg : args) {
                                                    String keyName = (String)arg;
                                                    try {
                                                        IOC.unregister(Keys.getKeyByName(keyName));
                                                    } catch (DeletionException e) {
                                                        System.out.println("[WARNING] Deregistration of key '"+keyName+"' failed.");
                                                    } catch (ResolutionException e) { }
                                                }
                                                return null;
                                            })
                            );                                    ;
                        } catch (RegistrationException | ResolutionException | InvalidArgumentException e) {
                            throw new ActionExecutionException("IOCKeys plugin can't load: can't register new strategy", e);
                        }
                    })
                    .revertProcess(() -> {
                        try {
                            String[] itemNames = { "ioc unregister by names for bootstrap" };
                            IOC.resolve(Keys.getKeyByName("ioc unregister by names for bootstrap"), itemNames);
                        } catch(ResolutionException e) { }
                    });

            bootstrap.add(iocDeregistrationsItem);
*/
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

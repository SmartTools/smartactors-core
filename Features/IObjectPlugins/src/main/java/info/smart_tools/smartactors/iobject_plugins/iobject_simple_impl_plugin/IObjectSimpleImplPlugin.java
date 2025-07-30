package info.smart_tools.smartactors.iobject_plugins.iobject_simple_impl_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.iobject.iobject_simple_implementation.IObjectImpl;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

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
                        IKey fieldKey = Keys.getKeyByName(IObjectImpl.class.getCanonicalName());
                        IOC.register(fieldKey, new ApplyFunctionToArgumentsStrategy(
                                (args) -> new IObjectImpl()
                        ));
                    } catch (ResolutionException e) {
                        throw new ActionExecutionException("IObjectSimpleImpl plugin can't load: can't get IObjectSimpleImpl key", e);
                    } catch (InvalidArgumentException e) {
                        throw new ActionExecutionException("IObjectSimpleImpl plugin can't load: can't create strategy", e);
                    } catch (RegistrationException e) {
                        throw new ActionExecutionException("IObjectSimpleImpl plugin can't load: can't register new strategy", e);
                    }
                })
                .revertProcess(() -> {
                    String[] keyNames = { IObjectImpl.class.getCanonicalName() };
                    Keys.unregisterByNames(keyNames);
                });
            bootstrap.add(item);
        } catch (InvalidArgumentException e) {
            throw new PluginException("Can't load IObject plugin", e);
        }
    }
}

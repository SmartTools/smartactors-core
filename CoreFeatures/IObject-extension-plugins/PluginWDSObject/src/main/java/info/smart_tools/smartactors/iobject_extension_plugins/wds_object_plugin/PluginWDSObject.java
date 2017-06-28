package info.smart_tools.smartactors.iobject_extension_plugins.wds_object_plugin;

import info.smart_tools.smartactors.feature_loading_system.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.base.strategy.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.IPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.iplugin.exception.PluginException;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.iobject_extension.wds_object.WDSObject;

/**
 * Plugin registers into IOC strategy for creation new instance of
 * {@link WDSObject} and strategy for storing other strategies that will be using as WDS transformation rules.
 */
public class PluginWDSObject implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap    the bootstrap
     */
    public PluginWDSObject(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {

        try {
            IBootstrapItem<String> wdsObjectItem = new BootstrapItem("wds_object");

            wdsObjectItem
                    .after("IOC")
                    .after("FieldNamePlugin")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(WDSObject.class.getCanonicalName()),
                                    new CreateNewInstanceStrategy(args -> {
                                        IObject config = (IObject) args[0];

                                        try {
                                            return new WDSObject(config);
                                        } catch (InvalidArgumentException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                            IOC.register(
                                    Keys.getOrAdd(IResolveDependencyStrategy.class.getCanonicalName()),
                                    new ResolveByNameIocStrategy(
                                            (a) -> a[1]
                                    )
                            );
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("WDSObject plugin can't load: can't get WDSObject key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("WDSObject plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("WDSObject plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(wdsObjectItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

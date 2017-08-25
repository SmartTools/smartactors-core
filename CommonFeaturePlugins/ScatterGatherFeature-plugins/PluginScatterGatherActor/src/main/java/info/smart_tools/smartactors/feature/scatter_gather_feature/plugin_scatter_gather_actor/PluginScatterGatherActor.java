package info.smart_tools.smartactors.feature.scatter_gather_feature.plugin_scatter_gather_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature.scatter_gather_feature.scatter_gather_actor.ScatterGatherActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin for register {@link ScatterGatherActor} to {@link IOC}
 */
public class PluginScatterGatherActor extends BootstrapPlugin {
    public PluginScatterGatherActor(IBootstrap bootstrap) {
        super(bootstrap);
    }

    @Item("register_scatter_gather_actor")
    public void registerScatterGatherActor() throws ResolutionException, InvalidArgumentException, RegistrationException {
        IFieldName actorName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "strategyDependency");
        IOC.register(Keys.getOrAdd("ScatterGatherActor"),
                new ApplyFunctionToArgumentsStrategy(
                        (args) -> {
                            try {
                                return new ScatterGatherActor(((IObject) args[0]));
                            } catch (ResolutionException | ReadValueException e) {
                                throw new RuntimeException(e);
                            }
                        }
                )
        );
    }
}

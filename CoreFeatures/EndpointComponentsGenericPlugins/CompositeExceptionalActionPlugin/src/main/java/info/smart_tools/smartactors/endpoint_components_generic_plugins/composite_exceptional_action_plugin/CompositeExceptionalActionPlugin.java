package info.smart_tools.smartactors.endpoint_components_generic_plugins.composite_exceptional_action_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.i_addition_dependency_strategy.IAdditionDependencyStrategy;
import info.smart_tools.smartactors.endpoint_components_generic.composite_exceptional_action.CompositeActionCreationStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;

/**
 * Plugin that registers a strategy parsing composite exceptional action description.
 */
public class CompositeExceptionalActionPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public CompositeExceptionalActionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    @Item("composite_endpoint_exceptional_action")
    @After({
            "default_generic_message_handlers",
    })
    public void registerActionStrategy() throws Exception {
        IAdditionDependencyStrategy storage = IOC.resolve(
                Keys.getOrAdd("expandable_strategy#exceptional endpoint action"));

        storage.register("composite", new CompositeActionCreationStrategy());
    }
}

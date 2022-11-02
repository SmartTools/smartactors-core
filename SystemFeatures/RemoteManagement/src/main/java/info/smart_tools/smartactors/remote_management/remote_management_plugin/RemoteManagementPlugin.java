package info.smart_tools.smartactors.remote_management.remote_management_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.remote_management.feature_load_starter_actor.FeatureLoadStarterActor;

public class RemoteManagementPlugin extends BootstrapPlugin {

     /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public RemoteManagementPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    /**
     * Registers dependencies to the IOC
     * @throws ResolutionException if any errors occurred on IOC resolution
     * @throws RegistrationException if any errors occurred on registration dependency to the IOC
     * @throws InvalidArgumentException if any errors occurred on creation objects
     */
    @Item("remote_management")
    public void register()
            throws ResolutionException, RegistrationException, InvalidArgumentException {

        IOC.register(Keys.getKeyByName("FeatureLoadStarterActor"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new FeatureLoadStarterActor();
            } catch (Exception e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    @ItemRevert("remote_management")
    public void unregister() {
        String[] itemNames = { "FeatureLoadStarterActor" };
        Keys.unregisterByNames(itemNames);
    }
}

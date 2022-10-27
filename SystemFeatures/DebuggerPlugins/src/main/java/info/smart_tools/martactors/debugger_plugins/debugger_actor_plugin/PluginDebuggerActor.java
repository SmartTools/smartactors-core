package info.smart_tools.martactors.debugger_plugins.debugger_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.debugger.actor.DebuggerActor;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class PluginDebuggerActor extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginDebuggerActor(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy of creation of a new debugger actor.
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws RegistrationException if error occurs registering a strategy
     * @throws InvalidArgumentException if strategy does not accept the function
     */
    @Item("debugger:actor")
    @After({
            "debugger:session",
    })
    public void registerActor()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("debugger actor"), new ApplyFunctionToArgumentsStrategy(args -> new DebuggerActor()));
    }
}

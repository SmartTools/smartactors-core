package info.smart_tools.smartactors.debugger_plugins.session_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.debugger.session_impl.DebuggerSessionImpl;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class PluginDebuggerSession extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginDebuggerSession(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy of creation of a debugger session.
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws RegistrationException if error occurs registering a strategy
     * @throws InvalidArgumentException if strategy does not accept the function
     */
    @Item("debugger:session")
    @After({"debugger:sequence", "debugger:breakpoint_storage"})
    public void registerSessionStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("debugger session"), new ApplyFunctionToArgumentsStrategy(args -> {
            String id = (String) args[0];
            Object addr = args[1];

            try {
                return new DebuggerSessionImpl(id, addr);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

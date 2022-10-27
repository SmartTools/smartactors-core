package info.smart_tools.smartactors.dumpable_plugins.make_dump;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable;
import info.smart_tools.smartactors.dumpable_interface.idumpable.exceptions.DumpException;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

/**
 *
 */
public class PluginMakeDump extends BootstrapPlugin {
    /**
     * The constructor.
     * @param bootstrap    the bootstrap
     */
    public PluginMakeDump(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register {@code "make dump"} strategy that calls {@link IDumpable#dump(IObject)} method.
     *
     * @throws ResolutionException if error occurs resolving IOC key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if {@link ApplyFunctionToArgumentsStrategy} does not accept passed function
     */
    @Item("dump_creation_strategy")
    public void registerDumpCreationStrategy()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("make dump"), new ApplyFunctionToArgumentsStrategy(args -> {
            IDumpable dumpable = (IDumpable) args[0];
            IObject options = (IObject) args[1];

            try {
                return dumpable.dump(options);
            } catch (DumpException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

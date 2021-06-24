package info.smart_tools.smartactors.debugger_plugins.debugger_sequence;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.debugger.sequence_impl.DebuggerSequenceImpl;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;

/**
 *
 */
public class PluginDebuggerSequence extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginDebuggerSequence(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy of creation of debugger sequence.
     *
     * @throws ResolutionException if error occurs resolving a key
     * @throws RegistrationException if error occurs registering a strategy
     * @throws InvalidArgumentException if strategy does not accept the function
     */
    @Item("debugger:sequence")
    @After({
            "core"
    })
    public void registerSequence()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("new debugger sequence"), new ApplyFunctionToArgumentsStrategy(args -> {
            IMessageProcessingSequence innerSeq = (IMessageProcessingSequence) args[0];
            Object debuggerAddress = args[1];

            try {
                return new DebuggerSequenceImpl(innerSeq, debuggerAddress);
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

package info.smart_tools.smartactors.statistics_plugins.statistics_manager_actor_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import info.smart_tools.smartactors.statistics.statistics_manager.StatisticsManagerActor;

/**
 *
 */
public class PluginStatisticsManagerActor extends BootstrapPlugin {
    /**
     * The constructor.
     *
     * @param bootstrap the bootstrap
     */
    public PluginStatisticsManagerActor(final IBootstrap bootstrap) {
        super(bootstrap);
    }

    /**
     * Register strategy for creation of new instance of statistics manager actor.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if strategy does not accept the function
     */
    @Item("statistics_manager_actor")
    public void registerActorCreationDependency()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("statistics manager actor"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new StatisticsManagerActor();
            } catch (ResolutionException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Dummy tatistics collector that prints everything it receives to stdout.
     *
     * @throws ResolutionException if error occurs resolving the key
     * @throws RegistrationException if error occurs registering the strategy
     * @throws InvalidArgumentException if strategy does not accept the function
     */
    @Item("test_statistics_collector")
    public void registerTestStatisticsCollector()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("test statistics collector"), new ApplyFunctionToArgumentsStrategy(args -> {
            try {
                return new IMessageReceiver() {
                    @Override
                    public void receive(final IMessageProcessor processor) throws MessageReceiveException, AsynchronousOperationException {
                        try {
                            String txt = processor.getMessage().serialize();
                            System.out.printf("Collector message: %s\n", txt);
                        } catch (Exception e) {
                            throw new MessageReceiveException(e);
                        }
                    }

                    @Override
                    public void dispose() { }
                };
            } catch (Exception e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }
}

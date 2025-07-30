package info.smart_tools.smartactors.shutdown_plugins.on_shutown_request_configuration_section_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

import java.util.List;

public class OnShutownRequestConfigurationSectionPlugin extends BootstrapPlugin {

    /**
     * The constructor.
     *
     * @param bootstrap    the bootstrap
     */
    public OnShutownRequestConfigurationSectionPlugin(final IBootstrap bootstrap) {
            super(bootstrap);
    }

    /**
     * Register default action for {@code "onShutdownRequest"} section.
     *
     * @throws ResolutionException if error occurs resolving key
     * @throws RegistrationException if error occurs registering strategy
     * @throws InvalidArgumentException if strategy does not accept function
     * TODO:: Simplify the strategy... Move MessageBus to core and use it may be..
     */
    public void registerDefaultOnShutdownRequestAction()
            throws ResolutionException, RegistrationException, InvalidArgumentException {
        IOC.register(Keys.getKeyByName("default shutdown request action"), new ApplyFunctionToArgumentsStrategy(args -> {
            IObject arg = (IObject) args[0];

            try {
                IFieldName messagesFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "messages");
                IFieldName chainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
                IFieldName modeFieldFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "modeField");

                List<IObject> messages = (List) arg.getValue(messagesFN);
                Object chainName = arg.getValue(chainFN);
                IFieldName modeFieldName = (null == arg.getValue(modeFieldFN)) ? null :
                        IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), arg.getValue(modeFieldFN));

                IChainStorage chainStorage = IOC.resolve(IOC.resolve(IOC.getKeyForKeyByNameStrategy(),
                        IChainStorage.class.getCanonicalName()));
                IQueue<ITask> queue = IOC.resolve(Keys.getKeyByName("task_queue"));

                Integer stackDepth0;

                try {
                    stackDepth0 = IOC.resolve(Keys.getKeyByName("default_stack_depth"));
                } catch (ResolutionException e) {
                    stackDepth0 = 5;
                }
                Integer stackDepth = stackDepth0;

                return (IAction<Object>) mode -> {
                    try {
                        for (IObject message : messages) {
                            if (null != modeFieldName) {
                                message.setValue(modeFieldName, mode);
                            }
                            IMessageProcessingSequence processingSequence = IOC.resolve(
                                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                                    stackDepth,
                                    chainName,
                                    message
                            );
                            IMessageProcessor messageProcessor = IOC.resolve(
                                    IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                                    queue,
                                    processingSequence
                            );
                            messageProcessor.process(message, (IObject) IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject")));
                        }
                    } catch (ResolutionException | ChangeValueException | MessageProcessorProcessException e) {
                        throw new ActionExecutionException(e);
                    }
                };
            } catch (ResolutionException | ReadValueException | InvalidArgumentException e) {
                throw new FunctionExecutionException(e);
            }
        }));
    }

    /**
     * Register strategy for {@code "onShutdownRequest"} config section.
     *
     * @throws ResolutionException if error occurs resolving key or any dependency
     * @throws InvalidArgumentException if strategy is not accepted by configuration manager
     */
    @Item("config_section:onShutdownRequest")
    @After({
        "config_section:maps",
        "config_section:objects",
        "config_sections:start",
        "core_upcounters_setup:done",
    })
    @Before({
        "config_sections:done",
    })
    public void registerOnShutdownRequestSectionProcessingStrategy()
            throws ResolutionException, InvalidArgumentException {
        IConfigurationManager configurationManager = IOC.resolve(Keys.getKeyByName(IConfigurationManager.class.getCanonicalName()));

        configurationManager.addSectionStrategy(
                new OnShutdownRequestConfigurationSectionStrategy("default shutdown request action"));
    }
}

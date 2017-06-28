package info.smart_tools.smartactors.shutdown_plugins.on_shutown_request_configuration_section_plugin;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.FunctionExecutionException;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.feature_loading_system.bootstrap_plugin.BootstrapPlugin;
import info.smart_tools.smartactors.feature_loading_system.interfaces.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
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
        IOC.register(Keys.getOrAdd("default shutdown request action"), new ApplyFunctionToArgumentsStrategy(args -> {
            IObject arg = (IObject) args[0];

            try {
                IFieldName messagesFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messages");
                IFieldName chainFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "chain");
                IFieldName modeFieldFN = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "modeField");

                List<IObject> messages = (List) arg.getValue(messagesFN);
                Object chainName = arg.getValue(chainFN);
                IFieldName modeFieldName = (null == arg.getValue(modeFieldFN)) ? null :
                        IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), arg.getValue(modeFieldFN));

                IChainStorage chainStorage = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(),
                        IChainStorage.class.getCanonicalName()));
                IQueue<ITask> queue = IOC.resolve(Keys.getOrAdd("task_queue"));

                Integer stackDepth0;

                try {
                    stackDepth0 = IOC.resolve(Keys.getOrAdd("default_stack_depth"));
                } catch (ResolutionException e) {
                    stackDepth0 = 5;
                }

                Integer stackDepth = stackDepth0;

                Object mapId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), chainName);
                IReceiverChain chain = chainStorage.resolve(mapId);

                return (IAction<Object>) mode -> {
                    try {
                        for (IObject message : messages) {
                            if (null != modeFieldName) {
                                message.setValue(modeFieldName, mode);
                            }

                            IMessageProcessingSequence processingSequence = IOC.resolve(
                                    IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessingSequence.class.getCanonicalName()),
                                    stackDepth,
                                    chain
                            );
                            IMessageProcessor messageProcessor = IOC.resolve(
                                    IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessor.class.getCanonicalName()),
                                    queue,
                                    processingSequence
                            );
                            messageProcessor.process(message, (IObject) IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName())));
                        }
                    } catch (ResolutionException | ChangeValueException | MessageProcessorProcessException e) {
                        throw new ActionExecuteException(e);
                    }
                };
            } catch (ResolutionException | ReadValueException | InvalidArgumentException | ChainNotFoundException e) {
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
        "config_sections:end",
    })
    public void registerOnShutdownRequestSectionProcessingStrategy()
            throws ResolutionException, InvalidArgumentException {
        IConfigurationManager configurationManager = IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

        configurationManager.addSectionStrategy(
                new OnShutdownRequestConfigurationSectionStrategy("default shutdown request action"));
    }
}

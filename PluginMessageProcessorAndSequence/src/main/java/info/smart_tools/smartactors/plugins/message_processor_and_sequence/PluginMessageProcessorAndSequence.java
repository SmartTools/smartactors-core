package info.smart_tools.smartactors.plugins.message_processor_and_sequence;

import info.smart_tools.smartactors.core.bootstrap_item.BootstrapItem;
import info.smart_tools.smartactors.core.create_new_instance_strategy.CreateNewInstanceStrategy;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.ibootstrap.IBootstrap;
import info.smart_tools.smartactors.core.ibootstrap_item.IBootstrapItem;
import info.smart_tools.smartactors.core.iconfiguration_manager.IConfigurationManager;
import info.smart_tools.smartactors.core.iioccontainer.exception.RegistrationException;
import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.iplugin.IPlugin;
import info.smart_tools.smartactors.core.iplugin.exception.PluginException;
import info.smart_tools.smartactors.core.iqueue.IQueue;
import info.smart_tools.smartactors.core.itask.ITask;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing_sequence.MessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processor.MessageProcessor;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;

/**
 *
 */
public class PluginMessageProcessorAndSequence implements IPlugin {
    private IBootstrap<IBootstrapItem<String>> bootstrap;

    /**
     * The constructor
     *
     * @param bootstrap    the bootstrap
     */
    public PluginMessageProcessorAndSequence(final IBootstrap<IBootstrapItem<String>> bootstrap) {
        this.bootstrap = bootstrap;
    }

    @Override
    public void load() throws PluginException {
        try {
            /* "message_processor" - register message processor creation strategy */
            IBootstrapItem<String> processorItem = new BootstrapItem("message_processor");

            processorItem
                    .after("IOC")
                    .after("wds_object")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(IMessageProcessor.class.getCanonicalName()),
                                    new CreateNewInstanceStrategy(args -> {
                                        IQueue<ITask> taskQueue = (IQueue<ITask>) args[0];
                                        IMessageProcessingSequence sequence = (IMessageProcessingSequence) args[1];
                                        IObject config;

                                        if (args.length > 2) {
                                            config = (IObject) args[2];
                                        } else {
                                            try {
                                                IConfigurationManager configurationManager =
                                                        IOC.resolve(Keys.getOrAdd(IConfigurationManager.class.getCanonicalName()));

                                                config = configurationManager.getConfig();
                                            } catch (ResolutionException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }

                                        try {
                                            return new MessageProcessor(taskQueue, sequence, config);
                                        } catch (InvalidArgumentException | ResolutionException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        } catch (ResolutionException e) {
                            throw new ActionExecuteException("MessageProcessorAndSequence plugin can't load: can't get MessageProcessorAndSequence key", e);
                        } catch (InvalidArgumentException e) {
                            throw new ActionExecuteException("MessageProcessorAndSequence plugin can't load: can't create strategy", e);
                        } catch (RegistrationException e) {
                            throw new ActionExecuteException("MessageProcessorAndSequence plugin can't load: can't register new strategy", e);
                        }
                    });

            bootstrap.add(processorItem);

            /* "message_processing_sequence" - register sequence creation strategy */
            IBootstrapItem<String> sequenceItem = new BootstrapItem("message_processing_sequence");

            sequenceItem
                    .after("IOC")
                    .process(() -> {
                        try {
                            IOC.register(
                                    Keys.getOrAdd(IMessageProcessingSequence.class.getCanonicalName()),
                                    new CreateNewInstanceStrategy(args -> {
                                        int stackDepth = ((Number) args[0]).intValue();
                                        IReceiverChain mainChain = (IReceiverChain) args[1];

                                        try {
                                            return new MessageProcessingSequence(stackDepth, mainChain);
                                        } catch (InvalidArgumentException | ResolutionException e) {
                                            throw new RuntimeException(e);
                                        }
                                    }));
                        } catch (ResolutionException | InvalidArgumentException | RegistrationException e) {
                            throw new ActionExecuteException(e);
                        }
                    });

            bootstrap.add(sequenceItem);
        } catch (InvalidArgumentException e) {
            throw new PluginException(e);
        }
    }
}

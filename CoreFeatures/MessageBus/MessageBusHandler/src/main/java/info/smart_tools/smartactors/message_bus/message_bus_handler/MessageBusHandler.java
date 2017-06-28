package info.smart_tools.smartactors.message_bus.message_bus_handler;

import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.IMessageBusHandler;
import info.smart_tools.smartactors.message_bus.interfaces.imessage_bus_handler.exception.MessageBusHandlerException;
import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.iresponse_strategy.IResponseStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class MessageBusHandler implements IMessageBusHandler {

    private final IFieldName replyToFieldName;
    private final IFieldName finalActionsFieldName;
    private final IFieldName responseStrategyFieldName;

    private final IQueue<ITask> taskQueue;
    private final int stackDepth;
    private final IReceiverChain chain;
    private final IAction<IObject> replyAction;

    private final IResponseStrategy messageBusResponseStrategy;
    private final IResponseStrategy nullResponseStrategy;

    /**
     *
     * @param taskQueue Queue of the tasks
     * @param stackDepth Stack depth of the {@link IMessageProcessor}
     * @param receiverChain the chain for processing incoming message
     * @param finalAction the final action for
     * @throws InvalidArgumentException if there is invalid arguments
     */
    public MessageBusHandler(final IQueue<ITask> taskQueue, final int stackDepth, final IReceiverChain receiverChain, final IAction<IObject> finalAction)
            throws InvalidArgumentException, ResolutionException {
        if (null == taskQueue) {
            throw new InvalidArgumentException("Task queue should not be null.");
        }
        if (stackDepth < 0) {
            throw new InvalidArgumentException("Stack depth should be positive number.");
        }
        if (null == receiverChain) {
            throw new InvalidArgumentException("ReceiverChain should not be null.");
        }
        if (null == finalAction)  {
            throw new InvalidArgumentException("FinalAction should not be null.");
        }
        this.stackDepth = stackDepth;
        this.taskQueue = taskQueue;
        this.chain = receiverChain;
        this.replyAction = finalAction;

        this.finalActionsFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "finalActions");
        this.replyToFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "messageBusReplyTo");
        this.responseStrategyFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "responseStrategy");

        this.messageBusResponseStrategy = IOC.resolve(Keys.getOrAdd("message bus response strategy"));
        this.nullResponseStrategy = IOC.resolve(Keys.getOrAdd("null response strategy"));
    }

    @Override
    public void handle(final IObject message)
            throws MessageBusHandlerException {
        handle0(message, this.chain);
    }

    @Override
    public void handle(final IObject message, final Object chainName)
            throws MessageBusHandlerException {
        handle0(message, resolveChain(chainName));
    }

    @Override
    public void handleForReply(final IObject message, final Object replyToChainName)
            throws MessageBusHandlerException {
        handleForReply0(message, this.chain, replyToChainName);
    }

    @Override
    public void handleForReply(final IObject message, final Object chainName, final Object replyToChainName)
            throws MessageBusHandlerException {
        handleForReply0(message, resolveChain(chainName), replyToChainName);
    }

    private void handle0(final IObject message, final IReceiverChain dstChain)
            throws MessageBusHandlerException {
        try {
            resolveMessageProcessor(dstChain)
                    .process(message, resolveDefaultContext());
        } catch (ResolutionException | InvalidArgumentException | ChangeValueException | MessageProcessorProcessException e) {
            throw new MessageBusHandlerException("Failed to handle message to MessageBus.", e);
        }
    }

    private void handleForReply0(final IObject message, final IReceiverChain dstChain, final Object replyChainId)
            throws MessageBusHandlerException {
        try {
            resolveMessageProcessor(dstChain)
                    .process(message, resolveReplyContext(replyChainId));
        } catch (ResolutionException | ChangeValueException | InvalidArgumentException | MessageProcessorProcessException e) {
            throw new MessageBusHandlerException("Failed to handle message to MessageBus.", e);
        }
    }

    private IReceiverChain resolveChain(final Object chainName)
            throws MessageBusHandlerException {
        try {
            Object chainId = IOC.resolve(Keys.getOrAdd("chain_id_from_map_name"), chainName);
            IChainStorage chainStorage = IOC.resolve(Keys.getOrAdd(IChainStorage.class.getCanonicalName()));

            return chainStorage.resolve(chainId);
        } catch (ResolutionException | ChainNotFoundException e) {
            throw new MessageBusHandlerException("Error occurred resolving target chain.", e);
        }
    }

    private IMessageProcessor resolveMessageProcessor(final IReceiverChain mpChain) throws ResolutionException {
        IMessageProcessingSequence processingSequence = IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessingSequence.class.getCanonicalName()),
                this.stackDepth,
                mpChain
        );
        return IOC.resolve(
                IOC.resolve(IOC.getKeyForKeyStorage(), IMessageProcessor.class.getCanonicalName()),
                this.taskQueue,
                processingSequence
        );
    }

    private IObject resolveDefaultContext()
            throws InvalidArgumentException, ResolutionException, ChangeValueException {
        IObject context = IOC.resolve(Keys.getOrAdd(IObject.class.getCanonicalName()));
        context.setValue(responseStrategyFieldName, nullResponseStrategy);
        return context;
    }

    private IObject resolveReplyContext(final Object replyChainId)
            throws InvalidArgumentException, ResolutionException, ChangeValueException {
        IObject context = resolveDefaultContext();

        context.setValue(responseStrategyFieldName, messageBusResponseStrategy);

        List<IAction> actionList = new ArrayList<>();
        actionList.add(this.replyAction);
        context.setValue(this.finalActionsFieldName, actionList);

        context.setValue(this.replyToFieldName, replyChainId);

        return context;
    }
}

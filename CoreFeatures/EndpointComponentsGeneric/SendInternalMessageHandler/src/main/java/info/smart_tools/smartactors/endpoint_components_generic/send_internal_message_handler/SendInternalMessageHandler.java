package info.smart_tools.smartactors.endpoint_components_generic.send_internal_message_handler;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IDefaultMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.ITerminalMessageHandler;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageProcessorProcessException;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;

/**
 * A {@link info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandler message handler} that
 * sends an internal message with environment represented by incoming destination message produced by previous handlers.
 *
 * @param <TSrc>
 * @param <TCtx>
 */
public class SendInternalMessageHandler<TSrc, TCtx>
        implements ITerminalMessageHandler<IDefaultMessageContext<TSrc, IObject, TCtx>> {
    private final IFieldName messageFieldName, contextFieldName;

    private final int stackDepth;
    private final IQueue<ITask> taskQueue;
    private final IReceiverChain receiverChain;

    /**
     * The constructor.
     *
     * @param stackDepth    message processing sequence stack depth
     * @param taskQueue     task queue to use to run message processor
     * @param receiverChain receiver chai to send the message to
     * @throws ResolutionException if error occurs resolving any dependencies
     */
    public SendInternalMessageHandler(final int stackDepth, final IQueue<ITask> taskQueue, final IReceiverChain receiverChain)
            throws ResolutionException {
        this.stackDepth = stackDepth;
        this.taskQueue = taskQueue;
        this.receiverChain = receiverChain;

        this.messageFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "message");
        this.contextFieldName = IOC.resolve(Keys.getOrAdd(IFieldName.class.getCanonicalName()), "context");
    }

    @Override
    public void handle(
            final IMessageHandlerCallback<IMessageContext> next,
            final IDefaultMessageContext<TSrc, IObject, TCtx> ctx)
            throws MessageHandlerException {
        try {
            IObject env = ctx.getDstMessage();

            IMessageProcessingSequence processingSequence =
                    IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"),
                            stackDepth, receiverChain);
            IMessageProcessor messageProcessor =
                    IOC.resolve(Keys.getOrAdd("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"),
                            taskQueue, processingSequence);

            IObject message = (IObject) env.getValue(this.messageFieldName);
            IObject context = (IObject) env.getValue(this.contextFieldName);

            // TODO: Refactor message processor to avoid duplicate environment object creation (and add a #process(IObject env) method)
            messageProcessor.process(message, context);
        } catch (ResolutionException | InvalidArgumentException | ReadValueException
                | MessageProcessorProcessException e) {
            throw new MessageHandlerException(e);
        }
    }
}

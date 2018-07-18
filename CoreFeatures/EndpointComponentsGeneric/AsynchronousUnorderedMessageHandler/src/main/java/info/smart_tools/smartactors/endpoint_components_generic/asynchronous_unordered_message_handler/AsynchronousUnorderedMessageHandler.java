package info.smart_tools.smartactors.endpoint_components_generic.asynchronous_unordered_message_handler;

import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageContext;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.IMessageHandlerCallback;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.exception.MessageHandlerException;
import info.smart_tools.smartactors.endpoint_interfaces.imessage_handler.helpers.IBypassMessageHandler;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import info.smart_tools.smartactors.task.interfaces.itask.exception.TaskExecutionException;

/**
 * A message handler that executes the next handler asynchronously with no guarantees on execution order or
 * synchronization i.e. consequent messages from single connection may be processed in different order or even in
 * parallel.
 *
 * <p>
 *  This handler type may be used to process messages asynchronously when messages produced by a connection are
 *  processed independently (e.g. UDP datagrams) or when next message cannot be received until the previous one is
 *  processed (e.g. HTTP request).
 * </p>
 *
 * @param <T> message context type
 */
public class AsynchronousUnorderedMessageHandler<T extends IMessageContext> implements IBypassMessageHandler<T> {
    private final IQueue<ITask> taskQueue;

    /**
     * The constructor.
     *
     * @param taskQueue task queue to post asynchronous task on
     */
    public AsynchronousUnorderedMessageHandler(final IQueue<ITask> taskQueue) {
        this.taskQueue = taskQueue;
    }

    @Override
    public void handle(final IMessageHandlerCallback<T> next, final T context) throws MessageHandlerException {
        try {
            taskQueue.put(() -> {
                try {
                    next.handle(context);
                } catch (MessageHandlerException e) {
                    throw new TaskExecutionException(e);
                }
            });
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

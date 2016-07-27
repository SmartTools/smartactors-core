package info.smart_tools.smartactors.core.actor_receiver;

import info.smart_tools.smartactors.core.iioccontainer.exception.ResolutionException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.AsynchronousOperationException;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * {@link IMessageReceiver} that performs all necessary synchronization for a nested receiver to be executed as actor.
 *
 * {@link ActorReceiver} provides synchronization only for synchronous operation of a child receiver i.e. if child
 * receiver starts an asynchronous operation (by not calling passed callback) it may be called again by {@link
 * ActorReceiver} just after {@link IMessageReceiver#receive} method of child returns.
 */
public class ActorReceiver implements IMessageReceiver {
    // It's preferred to use ConcurrentLinkedQueue that uses lock-free algorithms
    private final Queue<IMessageProcessor> queue;

    // Atomic flag. True if any message is being processed by childReceiver
    private final AtomicBoolean isBusy;

    private final IMessageReceiver childReceiver;

    /**
     * The constructor.
     *
     * @param childReceiver    the child receiver
     * @throws InvalidArgumentException if childReceiver is {@code null}.
     * @throws ResolutionException if resolution of any dependencies fails.
     */
    public ActorReceiver(final IMessageReceiver childReceiver)
            throws InvalidArgumentException, ResolutionException {
        if (null == childReceiver) {
            throw new InvalidArgumentException("Child receiver should not be null.");
        }

        this.childReceiver = childReceiver;

        this.queue = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), "actor_receiver_queue"));
        this.isBusy = IOC.resolve(IOC.resolve(IOC.getKeyForKeyStorage(), "actor_receiver_busyness_flag"));
    }

    @Override
    public void receive(final IMessageProcessor processor)
            throws MessageReceiveException, AsynchronousOperationException {
        Throwable syncException = null;

        if (isBusy.compareAndSet(false, true)) {
            try {
                childReceiver.receive(processor);
            } catch (Throwable e) {
                syncException = e;
            } finally {
                isBusy.set(false);
            }
        } else {
            processor.pauseProcess();
            queue.add(processor);
        }

        executeDelayed();

        if (null != syncException) {
            throw new MessageReceiveException("Failed to execute actor receiver.", syncException);
        }
    }

    private void executeDelayed() {
        while (!queue.isEmpty()) {
            if (isBusy.compareAndSet(false, true)) {
                IMessageProcessor mp;

                try {
                    while (null != (mp = queue.poll())) {
                        executeOne(mp);
                    }
                } finally {
                    isBusy.set(false);
                }
            } else {
                break;
            }
        }
    }

    private void executeOne(final IMessageProcessor messageProcessor) {
        Throwable exception = null;

        try {
            childReceiver.receive(messageProcessor);
        } catch (Throwable e) {
            exception = e;
        }

        try {
            messageProcessor.continueProcess(exception);
        } catch (Throwable e) {
            if (null != exception) {
                e.addSuppressed(exception);
            }

            catchCriticalException(e);
        }
    }

    /**
     * Handler for exceptions occurred in callbacks passed to receiver. Such exceptions (in some cases) can not be
     * rethrown because of the {@link #receive} call being completed.
     *
     * @param e    the exception
     */
    private void catchCriticalException(final Throwable e) {
        // TODO: Handle exception.
        // The exception cannot be rethrown as it is not caused by error in processing of current message but by error in processing of
        // another one.
        e.printStackTrace();
    }
}

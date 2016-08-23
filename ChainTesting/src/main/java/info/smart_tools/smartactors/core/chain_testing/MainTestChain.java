package info.smart_tools.smartactors.core.chain_testing;

import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.iaction.exception.ActionExecuteException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link IReceiverChain} used as a root chain for test messages. Calls a callback when chain execution completed.
 */
public class MainTestChain implements IReceiverChain {
    private IAction<Throwable> completionCallback;
    private IObject successReceiverArgs;
    private AtomicBoolean isCompleted;

    private IMessageReceiver successfulReceiver = mp -> {
        try {
            if (isCompleted.compareAndSet(false, true)) {
                completionCallback.execute(null);
            }
        } catch (ActionExecuteException | InvalidArgumentException e) {
            throw new MessageReceiveException(e);
        }
    };

    /**
     * The constructor.
     *
     * @param completionCallback    the callback that should be called when chain completes successful (with {@code null} as the only
     *                              argument) or with exception (with that exception as first argument)
     * @param successReceiverArgs   object that will e returned by {@link #getArguments(int)} for a receiver reached in case of successful
     *                              completion of a chain
     * @throws InvalidArgumentException if {@code completionCallback} is {@code null}
     */
    public MainTestChain(final IAction<Throwable> completionCallback, final IObject successReceiverArgs)
            throws InvalidArgumentException {
        if (null == completionCallback) {
            throw new InvalidArgumentException("Callback should not be null.");
        }

        this.completionCallback = completionCallback;
        this.successReceiverArgs = successReceiverArgs;

        this.isCompleted = new AtomicBoolean(false);
    }

    @Override
    public IMessageReceiver get(final int index) {
        return (index == 0) ? successfulReceiver : null;
    }

    @Override
    public IObject getArguments(final int index) {
        return (index == 0) ? successReceiverArgs : null;
    }

    @Override
    public String getName() {
        return "root test chain";
    }

    @Override
    public IReceiverChain getExceptionalChain(final Throwable exception) {
        try {
            if (isCompleted.compareAndSet(false, true)) {
                completionCallback.execute(exception);
            }
        } catch (ActionExecuteException | InvalidArgumentException e) { }

        return this;
    }
}

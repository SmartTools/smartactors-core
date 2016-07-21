package info.smart_tools.smartactors.core.message_processing;

import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.core.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.core.message_processing.exceptions.NoExceptionHandleChainException;

/**
 * Object managing order of operations executed on a single message.
 *
 * May be used for processing o new messages when processing of current one is finished (see {@link #reset()}).
 *
 * Contains reference to a {@link IReceiverChain} (called main chain) used to process new messages. New chains may be
 * substituted using {@link #callChain(IReceiverChain)} method.
 */
public interface IMessageProcessingSequence {
    /**
     * Start processing of (new) message from first receiver of main chain.
     */
    void reset();

    /**
     * Switch current receiver reference to next receiver that should receive the message.
     *
     * @return {@code true} if there is at least one more receiver that should receive the message and {@code false} if
     *                      not
     */
    boolean next();

    /**
     * Go to specific step at some level of stack of nested chains.
     *
     * @param level    the stack level to go to
     * @param step     the step at that stack level
     * @throws InvalidArgumentException if level is negative number
     * @throws InvalidArgumentException if level is higher than current stack level
     */
    void goTo(int level, int step) throws InvalidArgumentException;

    /**
     * Get the next receiver that should receive the message.
     *
     * @return the receiver or {@code null} if message processing is finished
     */
    IMessageReceiver getCurrentReceiver();

    /**
     * Get the arguments object that should be passed to current receiver.
     *
     * @return the arguments objects or {@code null} if message processing is finished
     */
    IObject getCurrentReceiverArguments();

    /**
     * Interrupt execution of current chain by execution of a given one and when it is completed continue the previous.
     * Puts the new chain into a stack.
     *
     * @param chain    the {@link IReceiverChain} to call
     * @throws NestedChainStackOverflowException if overflow of nested chains stack occurs
     */
    void callChain(final IReceiverChain chain)
            throws NestedChainStackOverflowException;

    /**
     * Switch to the chain that should be executed when exception occurs.
     *
     * Saves in context positions (stack indexes and step indexes) of the points in the sequence where the exception
     * occurred ({@code "causeLevel"} and {@code "causeStep"} fields) and where it was caught - position in the chain
     * defining the called exceptional chain ({@code "catchLevel"} and {@code "causeStep"} fields).
     *
     * @param exception     the occurred exception
     * @param context       the context to save positions
     * @throws NoExceptionHandleChainException when there is no such chain.
     * @throws NestedChainStackOverflowException when it is impossible to call that chain.
     * @throws ChangeValueException if error occurs during writing positions to context
     * @throws InvalidArgumentException if incoming argument is null
     * @see #callChain(IReceiverChain)
     */
    void catchException(final Throwable exception, final IObject context)
            throws NoExceptionHandleChainException, NestedChainStackOverflowException, ChangeValueException, InvalidArgumentException;
}

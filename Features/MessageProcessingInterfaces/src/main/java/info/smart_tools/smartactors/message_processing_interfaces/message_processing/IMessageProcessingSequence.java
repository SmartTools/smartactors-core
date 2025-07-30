package info.smart_tools.smartactors.message_processing_interfaces.message_processing;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NoExceptionHandleChainException;

/**
 * Object managing order of operations executed on a single message.
 *
 * May be used for processing o new messages when processing of current one is finished (see {@link #reset()}).
 *
 * Contains reference to a {@link IReceiverChain} (called main chain) used to process new messages. New chains may be
 * substituted using {@link #callChain(Object)} method.
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
     * Go to last step of main chains.
     *
     */
    void end();

    /**
     * Get current level in stack of nested chains.
     *
     * @return current level in stack of nested chains
     */
    int getCurrentLevel();

    /**
     * Get current step at specific level of stack of nested chains.
     *
     * @param level    the level
     * @return current step at given level
     * @throws InvalidArgumentException if level does not correspond to any active level of chains stack; i.e. if level is a negative number
     *                                  or is higher than index of current level
     */
    int getStepAtLevel(int level) throws InvalidArgumentException;

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
     * @param chainName    the name of chain to call
     * @param processor    the message processor to check access to chain
     * @throws NestedChainStackOverflowException if overflow of nested chains stack occurs
     */
    void callChainSecurely(Object chainName, IMessageProcessor processor)
            throws NestedChainStackOverflowException, ChainNotFoundException,
            ChainChoiceException;

    /**
     * Setup resoration chain name for sequence. When chain which such name is called by callChain method then
     * current scope and module are restored from this chain.
     *
     * @param chainName    the name of chain to call
     */
    void setScopeSwitchingChainName(Object chainName);

    /**
     * Interrupt execution of current chain by execution of a given one and when it is completed continue the previous.
     * Puts the new chain into a stack.
     *
     * @param chainName    the name of chain to call
     * @throws NestedChainStackOverflowException if overflow of nested chains stack occurs
     */
    void callChain(Object chainName)
            throws NestedChainStackOverflowException, ChainNotFoundException;

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
     * @throws ReadValueException if error occurs during reading data from exception description
     * @see #callChainSecurely(Object, IMessageProcessor)
     */
    void catchException(Throwable exception, IObject context)
            throws NoExceptionHandleChainException, NestedChainStackOverflowException, ChangeValueException,
            InvalidArgumentException, ReadValueException, ChainNotFoundException;
}

package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

import java.text.MessageFormat;

/**
 * Exception thrown by {@link
 * IMessageProcessingSequence#catchException(Throwable, IObject)}
 * when no one of {@link IReceiverChain}'s in the stack defines an
 * exceptional chain for occurred exception.
 */
public class NoExceptionHandleChainException extends Exception {
    private final IObject[] chainsStack;
    private final int[] stepsStack;

    /**
     * The constructor.
     *
     * @param cause          the occurred exception no chain to handle found for
     * @param chainsStack    stack of chains where found no exceptional chain
     * @param stepsStack     steps in chains of stack where exception occurred
     */
    public NoExceptionHandleChainException(final Throwable cause, final IReceiverChain[] chainsStack, final int[] stepsStack) {
        super(
                MessageFormat.format("No exceptional chain found for exception occurred at step {0} of chain ''{1}''.",
                        (stepsStack.length != 0) ? stepsStack[stepsStack.length - 1]+1 : "<none>",
                        (chainsStack.length != 0) ? chainsStack[chainsStack.length - 1].getId() : "<none>"),
                cause);

        this.chainsStack = new IObject[chainsStack.length];

        for (int i = 0; i < chainsStack.length; i++) {
            this.chainsStack[i] = chainsStack[i].getChainDescription();
        }

        this.stepsStack = stepsStack;
    }

    public IObject[] getChainsStack() {
        return chainsStack;
    }

    public int[] getStepsStack() {
        return stepsStack;
    }
}

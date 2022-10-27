package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;

/**
 * Exception thrown by
 * {@link IMessageProcessingSequence#callChain(Object)}
 * when stack overflow occurs.
 */
public class NestedChainStackOverflowException extends Exception {
    private final IReceiverChain[] chainsStack;
    private final int[] stepsStack;

    /**
     * The constructor.
     *
     * @param chainsStack    stack of chains where found no exceptional chain
     * @param stepsStack     steps in chains of stack where exception occurred
     */
    public NestedChainStackOverflowException(final IReceiverChain[] chainsStack, final int[] stepsStack) {
        super("Overflow of stack of nested receiver chains occurred.");

        this.chainsStack = chainsStack;
        this.stepsStack = stepsStack;
    }

    public IReceiverChain[] getChainsStack() {
        return chainsStack;
    }

    public int[] getStepsStack() {
        return stepsStack;
    }
}

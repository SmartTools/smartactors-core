package info.smart_tools.smartactors.core.message_processing.exceptions;

import info.smart_tools.smartactors.core.message_processing.IReceiverChain;

/**
 * Exception thrown by
 * {@link info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence#callChain(IReceiverChain)}
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

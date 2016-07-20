package info.smart_tools.smartactors.core.message_processing.exceptions;

import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test for {@link NoExceptionHandleChainException}.
 */
public class NoExceptionHandleChainExceptionTest {
    @Test
    public void Should_getConstructedAndStoreAttributes()
            throws Exception {
        IReceiverChain[] receiverChainsStack = new IReceiverChain[0];
        int[] stepsStack = new int[0];
        Throwable cause = new Throwable();
        NoExceptionHandleChainException exception = new NoExceptionHandleChainException(cause, receiverChainsStack, stepsStack);

        assertSame(receiverChainsStack, exception.getChainsStack());
        assertSame(stepsStack, exception.getStepsStack());
        assertSame(cause, exception.getCause());
    }
}

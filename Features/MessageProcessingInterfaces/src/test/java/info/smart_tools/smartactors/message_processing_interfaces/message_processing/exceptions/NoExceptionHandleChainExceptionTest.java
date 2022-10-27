package info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions;

import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Test for {@link NoExceptionHandleChainException}.
 */
public class NoExceptionHandleChainExceptionTest {
    @Test
    public void Should_getConstructedAndStoreAttributes()
            throws Exception {
        IObject descMock = mock(IObject.class);
        IReceiverChain[] receiverChainsStack = new IReceiverChain[]{mock(IReceiverChain.class)};
        int[] stepsStack = new int[]{0};
        Throwable cause = new Throwable();
        when(receiverChainsStack[0].getChainDescription()).thenReturn(descMock);

        NoExceptionHandleChainException exception = new NoExceptionHandleChainException(cause, receiverChainsStack, stepsStack);

        assertSame(stepsStack, exception.getStepsStack());
        assertArrayEquals(new IObject[] {descMock}, exception.getChainsStack());
        assertSame(cause, exception.getCause());
    }
}

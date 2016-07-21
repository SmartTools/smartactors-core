package info.smart_tools.smartactors.core.exception_handling_receivers;

import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import org.junit.Test;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link SkipReceiverExceptionHandler}
 */
public class SkipReceiverExceptionHandlerTest extends ExceptionHandlingReceiverTest {
    @Test
    public void Should_SkipTheReceiverThrownAnException()
            throws Exception {
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        when(contextMock.getValue(same(causeLevelFieldName))).thenReturn(137);
        when(contextMock.getValue(same(causeStepFieldName))).thenReturn(123);

        IMessageReceiver receiver = new SkipReceiverExceptionHandler();

        receiver.receive(messageProcessorMock, mock(IObject.class), callbackMock);

        verify(sequenceMock).goTo(eq(137), eq(124));

        verify(callbackMock).execute(null);
    }

    @Test
    public void Should_WrapCaughtExceptions()
            throws Exception {
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);
        Exception exception = new ReadValueException();

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        when(contextMock.getValue(same(causeLevelFieldName))).thenThrow(exception);
        when(contextMock.getValue(same(causeStepFieldName))).thenReturn(123);

        IMessageReceiver receiver = new SkipReceiverExceptionHandler();

        try {
            receiver.receive(messageProcessorMock, mock(IObject.class), callbackMock);
            fail();
        } catch (MessageReceiveException e) {
            assertSame(exception, e.getCause());
        }
    }
}

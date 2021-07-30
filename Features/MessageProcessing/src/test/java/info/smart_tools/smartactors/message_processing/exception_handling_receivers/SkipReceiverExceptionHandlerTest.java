package info.smart_tools.smartactors.message_processing.exception_handling_receivers;

import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PowerMockIgnore;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link SkipReceiverExceptionHandler}
 */
@PowerMockIgnore("jdk.internal.reflect.*")
public class SkipReceiverExceptionHandlerTest extends ExceptionHandlingReceiverTest {
    @Test
    public void Should_SkipTheReceiverThrownAnException()
            throws Exception {
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);

        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        when(contextMock.getValue(same(causeLevelFieldName))).thenReturn(137);
        when(contextMock.getValue(same(causeStepFieldName))).thenReturn(123);

        IMessageReceiver receiver = new SkipReceiverExceptionHandler();

        receiver.receive(messageProcessorMock);

        verify(sequenceMock).goTo(eq(137), eq(124));

        receiver.dispose();
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
            receiver.receive(messageProcessorMock);
            fail();
        } catch (MessageReceiveException e) {
            assertSame(exception, e.getCause());
        }
    }
}

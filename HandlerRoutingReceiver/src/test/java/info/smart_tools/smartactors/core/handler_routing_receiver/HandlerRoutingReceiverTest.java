package info.smart_tools.smartactors.core.handler_routing_receiver;

import info.smart_tools.smartactors.core.ifield.IField;
import info.smart_tools.smartactors.core.ikey.IKey;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Tests for {@link HandlerRoutingReceiver}.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(IOC.class)
public class HandlerRoutingReceiverTest {
    private Map<Object, IMessageReceiver> mapMock;
    private IMessageReceiver receiverMock;
    private IField handlerFieldMock;
    private IMessageProcessor messageProcessorMock;
    private IObject argsMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;

    @Before
    public void setUp()
            throws Exception {
        IKey fieldKey = mock(IKey.class);

        mapMock = mock(Map.class);
        receiverMock = mock(IMessageReceiver.class);
        handlerFieldMock = mock(IField.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        argsMock = mock(IObject.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);

        mockStatic(IOC.class);
        when(IOC.getKeyForKeyStorage()).thenReturn(mock(IKey.class));
        when(IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName())).thenReturn(fieldKey);
        when(IOC.resolve(fieldKey, "handler")).thenReturn(handlerFieldMock);

        when(messageProcessorMock.getSequence()).thenReturn(messageProcessingSequenceMock);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(argsMock);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_handlersMapIsNull()
            throws Exception {
        assertNotNull(new HandlerRoutingReceiver(null));
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_wrapException_WhenFieldThrows()
            throws Exception {
        when(handlerFieldMock.in(same(argsMock))).thenThrow(new ReadValueException());

        new HandlerRoutingReceiver(mapMock).receive(messageProcessorMock);
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_throw_WhenHandlerReceiverIsNotFound()
            throws Exception {
        when(handlerFieldMock.in(same(argsMock))).thenReturn("notExist");
        when(mapMock.get(eq("notExist"))).thenReturn(null);

        new HandlerRoutingReceiver(mapMock).receive(messageProcessorMock);
    }

    @Test
    public void Should_callNestedReceiver_WhenItExist()
            throws Exception {
        IMessageReceiver theReceiverMock = mock(IMessageReceiver.class);

        when(handlerFieldMock.in(same(argsMock))).thenReturn("theReceiver");
        when(mapMock.get(eq("theReceiver"))).thenReturn(theReceiverMock);

        new HandlerRoutingReceiver(mapMock).receive(messageProcessorMock);

        verify(theReceiverMock).receive(same(messageProcessorMock));
    }
}

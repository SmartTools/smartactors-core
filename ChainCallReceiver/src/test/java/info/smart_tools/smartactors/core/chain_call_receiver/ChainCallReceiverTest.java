package info.smart_tools.smartactors.core.chain_call_receiver;

import info.smart_tools.smartactors.core.chain_call_receiver.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.core.iaction.IAction;
import info.smart_tools.smartactors.core.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.message_processing.IReceiverChain;
import info.smart_tools.smartactors.core.message_processing.exceptions.MessageReceiveException;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ChainCallReceiver}.
 */
public class ChainCallReceiverTest {
    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenChainStorageIsNull()
            throws Exception {
        new ChainCallReceiver(null, mock(IChainChoiceStrategy.class));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenStrategyIsNull()
            throws Exception {
        new ChainCallReceiver(mock(IChainStorage.class), null);
    }

    @Test
    public void Should_applyStrategy()
            throws Exception {
        IChainStorage chainStorageMock = mock(IChainStorage.class);
        IChainChoiceStrategy chainChoiceStrategyMock = mock(IChainChoiceStrategy.class);

        Object chainIdMock = mock(Object.class);
        IReceiverChain chainMock = mock(IReceiverChain.class);
        IMessageProcessor messageProcessorMock = mock(IMessageProcessor.class);
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);
        IAction actionMock = mock(IAction.class);
        ChainChoiceException exceptionMock = mock(ChainChoiceException.class);

        IMessageReceiver receiver = new ChainCallReceiver(chainStorageMock, chainChoiceStrategyMock);

        when(chainChoiceStrategyMock.chooseChain(same(messageProcessorMock))).thenReturn(chainIdMock);
        when(chainStorageMock.resolve(same(chainIdMock))).thenReturn(chainMock);
        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        receiver.receive(messageProcessorMock, null, actionMock);

        verify(sequenceMock).callChain(same(chainMock));
        verify(actionMock).execute(isNull());

        when(chainChoiceStrategyMock.chooseChain(same(messageProcessorMock))).thenThrow(exceptionMock);

        try {
            receiver.receive(messageProcessorMock, null, actionMock);
            fail();
        } catch (MessageReceiveException e) {
            assertSame(exceptionMock, e.getCause());
        }
    }
}

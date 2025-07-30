package info.smart_tools.smartactors.message_processing.wrapper_creator_receiver_decorator;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.MessageReceiveException;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertSame;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;


public class WrapperCreatorReceiverDecoratorTest extends IOCInitializer {
    private IStrategy wrapperResolutionStrategyMock;
    private IStrategy wrapperResolutionStrategyResolutionStrategyMock;
    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence sequenceMock;
    private IObject envMock, wrapperMock, stepConfMock, wrapperConfMock;
    private Map<Object, IStrategy> map;
    private IMessageReceiver receiverMock;

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        wrapperResolutionStrategyMock = mock(IStrategy.class);
        wrapperResolutionStrategyResolutionStrategyMock = mock(IStrategy.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        sequenceMock = mock(IMessageProcessingSequence.class);
        envMock = mock(IObject.class);
        wrapperMock = mock(IObject.class);
        stepConfMock = mock(IObject.class);
        wrapperConfMock = mock(IObject.class);

        IOC.register(Keys.getKeyByName("the wrapper resolution strategy resolution strategy"),
                wrapperResolutionStrategyResolutionStrategyMock);

        when(wrapperResolutionStrategyResolutionStrategyMock.resolve(same(wrapperConfMock)))
                .thenReturn(wrapperResolutionStrategyMock)
                .thenThrow(StrategyException.class);

        when(messageProcessorMock.getEnvironment()).thenReturn(envMock);
        when(messageProcessorMock.getSequence()).thenReturn(sequenceMock);

        when(wrapperResolutionStrategyMock.resolve(same(envMock)))
                .thenReturn(wrapperMock);

        when(sequenceMock.getCurrentReceiverArguments()).thenReturn(stepConfMock);
        when(stepConfMock.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "wrapper"))).thenReturn(wrapperConfMock);

        receiverMock = mock(IMessageReceiver.class);

        map = new HashMap<>();
    }

    @Test
    public void Should_resolveWrapperAndPushAsEnvironment()
            throws Exception {
        IMessageReceiver decorator = new WrapperCreatorReceiverDecorator(
                receiverMock, map, "the wrapper resolution strategy resolution strategy");

        decorator.receive(messageProcessorMock);

        verify(messageProcessorMock, times(1)).pushEnvironment(same(wrapperMock));
        verify(wrapperResolutionStrategyResolutionStrategyMock, times(1)).resolve(same(wrapperConfMock));
        assertSame(wrapperResolutionStrategyMock, map.get(stepConfMock));
        decorator.receive(messageProcessorMock);

        verify(messageProcessorMock, times(2)).pushEnvironment(same(wrapperMock));
        verify(wrapperResolutionStrategyResolutionStrategyMock, times(1)).resolve(same(wrapperConfMock));
        assertSame(wrapperResolutionStrategyMock, map.get(stepConfMock));

        decorator.dispose();
        verify(receiverMock).dispose();

        doThrow(new RuntimeException("test")).when(receiverMock).dispose();
        decorator.dispose();
    }

    @Test(expected = MessageReceiveException.class)
    public void Should_wrapExceptions()
            throws Exception {
        IMessageReceiver decorator = new WrapperCreatorReceiverDecorator(
                receiverMock, map, "the wrapper resolution strategy resolution strategy");

        when(wrapperResolutionStrategyResolutionStrategyMock.resolve(any()))
                .thenThrow(StrategyException.class);

        decorator.receive(messageProcessorMock);
    }
}

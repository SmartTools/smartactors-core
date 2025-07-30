package info.smart_tools.smartactors.message_processing.condition_chain_choice_strategy;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_call_receiver.IChainChoiceStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ConditionChainChoiceStrategyTest extends IOCInitializer {
    private IStrategy chainIdStrategy;
    private IMessageProcessor messageProcessorMock;
    private IMessageProcessingSequence messageProcessingSequenceMock;
    private Object trueId = new Object();
    private Object falseId = new Object();

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks()
            throws Exception {
        chainIdStrategy = mock(IStrategy.class);
        messageProcessorMock = mock(IMessageProcessor.class);
        messageProcessingSequenceMock = mock(IMessageProcessingSequence.class);
        IObject args = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'trueChain':'trueChainId', 'falseChain':'falseChainId'}".replace('\'', '"'));

        when(messageProcessorMock.getSequence()).thenReturn(messageProcessingSequenceMock);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenReturn(args);

        when(chainIdStrategy.resolve(eq("trueChainId"))).thenReturn(trueId);
        when(chainIdStrategy.resolve(eq("falseChainId"))).thenReturn(falseId);

        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), chainIdStrategy);
    }

    @Test
    public void Should_chooseChainConditionIsTrue()
            throws Exception {
        IObject messageArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'chainCondition': true}".replace('\'', '"'));
        when(messageProcessorMock.getMessage()).thenReturn(messageArgs);
        IChainChoiceStrategy strategy = new ConditionChainChoiceStrategy();

        assertEquals("trueChainId", strategy.chooseChain(messageProcessorMock));
    }

    @Test
    public void Should_chooseChainConditionIsFalse()
            throws Exception {
        IObject messageArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'chainCondition': false}".replace('\'', '"'));
        when(messageProcessorMock.getMessage()).thenReturn(messageArgs);
        IChainChoiceStrategy strategy = new ConditionChainChoiceStrategy();

        assertEquals("falseChainId", strategy.chooseChain(messageProcessorMock));
    }



    @Test(expected = ReadValueException.class)
    public void Should_ThrowException()
            throws Exception {
        IObject messageArgs = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{'chainCondition': false}".replace('\'', '"'));
        when(messageProcessorMock.getMessage()).thenReturn(messageArgs);
        when(messageProcessingSequenceMock.getCurrentReceiverArguments()).thenThrow(ReadValueException.class);

        IChainChoiceStrategy strategy = new ConditionChainChoiceStrategy();

        strategy.chooseChain(messageProcessorMock);
    }
}

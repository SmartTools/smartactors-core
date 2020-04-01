package info.smart_tools.smartactors.checkpoint.recover_strategies;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link ReSendRestoringSequenceRecoverStrategy}.
 */
public class ReSendRestoringSequenceRecoverStrategyTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }
    @Test
    public void Should_makeDumpOfMessageProcessingSequenceOnInitialization()
            throws Exception {
        IObject stateMock = mock(IObject.class);
        IObject argsMock = mock(IObject.class);
        IObject dumpMock = mock(IObject.class);
        IMessageProcessor processorMock = mock(IMessageProcessor.class);
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);
        IStrategy makeDumpStrategy = mock(IStrategy.class);

        IOC.register(Keys.getKeyByName("make dump"), makeDumpStrategy);

        when(makeDumpStrategy.resolve(same(sequenceMock), same(argsMock))).thenReturn(dumpMock);

        when(processorMock.getSequence()).thenReturn(sequenceMock);

        new ReSendRestoringSequenceRecoverStrategy().init(stateMock, argsMock, processorMock);

        verify(stateMock).setValue(eq(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "sequenceDump")), same(dumpMock));
    }

    @Test
    public void Should_recoverMessageProcessingSequence()
            throws Exception {
        IObject state = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'responsibleCheckpointId':'rcid'," +
                        "'entryId':'eid'," +
                        "'prevCheckpointEntryId':'pceid'," +
                        "'prevCheckpointId':'pcid'," +
                        "'sequenceDump':'This is a sequence dump. Trust me I am IObject.'," +
                        "'message':{'is-a-message':true}" +
                        "}").replace('\'','"'));

        IStrategy recoverSequenceStrategy = mock(IStrategy.class);
        IStrategy messageProcessorStrategy = mock(IStrategy.class);
        IMessageProcessingSequence sequenceMock = mock(IMessageProcessingSequence.class);
        IMessageProcessor processorMock = mock(IMessageProcessor.class);
        Object taskQueue = new Object();

        IOC.register(Keys.getKeyByName("recover message processing sequence"), recoverSequenceStrategy);
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"), messageProcessorStrategy);
        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueue));

        when(recoverSequenceStrategy.resolve(eq("This is a sequence dump. Trust me I am IObject."), any())).thenReturn(sequenceMock);
        when(messageProcessorStrategy.resolve(same(taskQueue), same(sequenceMock))).thenReturn(processorMock);

        new ReSendRestoringSequenceRecoverStrategy().reSend(state);

        ArgumentCaptor<IObject> mc = ArgumentCaptor.forClass(IObject.class);

        verify(processorMock).process(mc.capture(), any());

        assertNotNull(mc.getValue());
        assertEquals(true, mc.getValue().getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "is-a-message")));

        IObject checkpointStatus =
                (IObject) mc.getValue().getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointStatus"));

        assertNotNull(checkpointStatus);
        assertEquals("rcid",
                checkpointStatus.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "responsibleCheckpointId")));
        assertEquals("eid",
                checkpointStatus.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "checkpointEntryId")));
        assertEquals("pceid",
                checkpointStatus.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointEntryId")));
        assertEquals("pcid",
                checkpointStatus.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "prevCheckpointId")));
    }
}

package info.smart_tools.smartactors.on_feature_loading_service_starter.on_feature_loading_starter;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.ISectionStrategy;
import info.smart_tools.smartactors.configuration_manager.interfaces.iconfiguration_manager.exceptions.ConfigurationProcessingException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ikey.IKey;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

/**
 * Tests
 */
public class OnFeatureLoadingSectionProcessingStrategyTest extends IOCInitializer {

    private IQueue<ITask> taskQueue;
    private IChainStorage chainStorage;
    private IStrategy sequenceResolveStrategy;
    private IStrategy chainMapIdResolveStrategy;
    private IMessageProcessingSequence sequence;
    private IMessageProcessor messageProcessor;
    private int stackDepth;
    private IObject context;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Before
    public void init()
            throws Exception {
        this.taskQueue = mock(IQueue.class);
        this.chainStorage = mock(IChainStorage.class);
        this.context = mock(IObject.class);

        IKey taskQueueKey = Keys.getKeyByName("task_queue");
        IOC.register(taskQueueKey,
                new SingletonStrategy(this.taskQueue));

        IKey chainStorageKey = Keys.getKeyByName(IChainStorage.class.getCanonicalName());
        IOC.register(chainStorageKey,
                new SingletonStrategy(this.chainStorage));

        IKey messageProcessorKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor");
        this.messageProcessor = mock(IMessageProcessor.class);
        IOC.register(messageProcessorKey, new SingletonStrategy(this.messageProcessor));

        IKey stackDepthKey = Keys.getKeyByName("default_stack_depth");
        this.stackDepth = 5;
        IOC.register(stackDepthKey, new SingletonStrategy(this.stackDepth));

        IOC.register(
                IOC.resolve(IOC.getKeyForKeyByNameStrategy(), "info.smart_tools.smartactors.iobject.iobject.IObject"),
                new SingletonStrategy(this.context)
        );
    }

    @Test
    public void checkGetSectionNameMethod()
            throws Exception {
        ISectionStrategy strategy = new OnFeatureLoadingSectionProcessingStrategy();
        assertEquals(strategy.getSectionName(), new FieldName("onFeatureLoading"));
    }

    @Test
    public void checkOnLoadConfigMethod()
            throws Exception {
        ISectionStrategy strategy = new OnFeatureLoadingSectionProcessingStrategy();

        IObject config = new DSObject("{\"onFeatureLoading\": [\n" +
                "    {\n" +
                "      \"chain\": \"chain1\",\n" +
                "      \"revert\": false,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"key\": \"value1\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"chain\": \"chain2\",\n" +
                "      \"revert\": false,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"key\": \"value2\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"value3\"\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"chain\": \"chain3\",\n" +
                "      \"revert\": true,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"key\": \"value2\"\n" +
                "        },\n" +
                "        {\n" +
                "          \"key\": \"value3\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]}");
        List<IObject> onFeatureLoadingConfigSection = (List<IObject>) config.getValue(new FieldName("onFeatureLoading"));
        IObject message11 = ((List<IObject>)onFeatureLoadingConfigSection.get(0).getValue(new FieldName("messages"))).get(0);
        IObject message21 = ((List<IObject>)onFeatureLoadingConfigSection.get(1).getValue(new FieldName("messages"))).get(0);
        IObject message22 = ((List<IObject>)onFeatureLoadingConfigSection.get(1).getValue(new FieldName("messages"))).get(1);
        IObject message31 = ((List<IObject>)onFeatureLoadingConfigSection.get(2).getValue(new FieldName("messages"))).get(0);
        IObject message32 = ((List<IObject>)onFeatureLoadingConfigSection.get(2).getValue(new FieldName("messages"))).get(1);

        this.chainMapIdResolveStrategy = mock(IStrategy.class);
        Object mapId1 = mock(Object.class);
        Object mapId2 = mock(Object.class);
        Object mapId3 = mock(Object.class);
        IReceiverChain chain1 = mock(IReceiverChain.class);
        IReceiverChain chain2 = mock(IReceiverChain.class);
        IReceiverChain chain3 = mock(IReceiverChain.class);
        when(this.chainMapIdResolveStrategy.resolve("chain1")).thenReturn(mapId1);
        when(this.chainMapIdResolveStrategy.resolve("chain2")).thenReturn(mapId2);
        when(this.chainMapIdResolveStrategy.resolve("chain3")).thenReturn(mapId3);

        IKey chainIdFromMapNameKey = Keys.getKeyByName("chain_id_from_map_name_and_message");
        IOC.register(chainIdFromMapNameKey,
                this.chainMapIdResolveStrategy);

        when(this.chainStorage.resolve(mapId1)).thenReturn(chain1);
        when(this.chainStorage.resolve(mapId2)).thenReturn(chain2);
        when(this.chainStorage.resolve(mapId3)).thenReturn(chain3);

        this.sequenceResolveStrategy = mock(IStrategy.class);
        IKey sequenceKey = Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence");
        this.sequence = mock(IMessageProcessingSequence.class);
        IOC.register(sequenceKey, this.sequenceResolveStrategy);
        when(this.sequenceResolveStrategy.resolve(this.stackDepth, "chain1", message11)).thenReturn(sequence);
        when(this.sequenceResolveStrategy.resolve(this.stackDepth, "chain2", message21)).thenReturn(sequence);
        when(this.sequenceResolveStrategy.resolve(this.stackDepth, "chain2", message22)).thenReturn(sequence);
        when(this.sequenceResolveStrategy.resolve(this.stackDepth, "chain3", message31)).thenReturn(sequence);
        when(this.sequenceResolveStrategy.resolve(this.stackDepth, "chain3", message32)).thenReturn(sequence);

        strategy.onLoadConfig(config);

//        verify(this.chainMapIdResolveStrategy, times(1)).resolve("chain1");
//        verify(this.chainMapIdResolveStrategy, times(1)).resolve("chain2");
//        verify(this.chainMapIdResolveStrategy, times(0)).resolve("chain3");

//        verify(this.chainStorage, times(1)).resolve(mapId1);
//        verify(this.chainStorage, times(1)).resolve(mapId2);
//        verify(this.chainStorage, times(0)).resolve(mapId3);

        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain1", message11, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain2", message21, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain2", message22, true);
        verify(this.sequenceResolveStrategy, times(0)).resolve(this.stackDepth, "chain3", message31, true);
        verify(this.sequenceResolveStrategy, times(0)).resolve(this.stackDepth, "chain3", message32, true);

        verify(this.messageProcessor, times(1)).process(message11, this.context);
        verify(this.messageProcessor, times(1)).process(message21, this.context);
        verify(this.messageProcessor, times(1)).process(message22, this.context);
        verify(this.messageProcessor, times(0)).process(message31, this.context);
        verify(this.messageProcessor, times(0)).process(message32, this.context);

        strategy.onRevertConfig(config);

//        verify(this.chainMapIdResolveStrategy, times(1)).resolve("chain1");
//        verify(this.chainMapIdResolveStrategy, times(1)).resolve("chain2");
//        verify(this.chainMapIdResolveStrategy, times(1)).resolve("chain3");

//        verify(this.chainStorage, times(1)).resolve(mapId1);
//        verify(this.chainStorage, times(1)).resolve(mapId2);
//        verify(this.chainStorage, times(1)).resolve(mapId3);

        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain1", message11, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain2", message21, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain2", message22, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain3", message31, true);
        verify(this.sequenceResolveStrategy, times(1)).resolve(this.stackDepth, "chain3", message32, true);

        verify(this.messageProcessor, times(1)).process(message11, this.context);
        verify(this.messageProcessor, times(1)).process(message21, this.context);
        verify(this.messageProcessor, times(1)).process(message22, this.context);
        verify(this.messageProcessor, times(1)).process(message31, this.context);
        verify(this.messageProcessor, times(1)).process(message32, this.context);
    }

    @Test (expected = ConfigurationProcessingException.class)
    public void checkConfigurationProcessingExceptionOnWrongConfig()
            throws Exception {
        ISectionStrategy strategy = new OnFeatureLoadingSectionProcessingStrategy();
        IObject config = new DSObject("{\"onFeatureLoading\": [\n" +
                "    {\n" +
                "      \"chain\": \"unknownChain\",\n" +
                "      \"revert\": false,\n" +
                "      \"messages\": [\n" +
                "        {\n" +
                "          \"key\": \"value\"\n" +
                "        }\n" +
                "      ]\n" +
                "    }]}");
        strategy.onLoadConfig(config);
        fail();
    }

}

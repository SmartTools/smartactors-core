package info.smart_tools.smartactors.feature_management.feature_manager_actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.exception.FeatureManagementException;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.AddFeatureWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.FeatureManagerStateWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.OnFeatureLoadedWrapper;
import info.smart_tools.smartactors.feature_management.feature_manager_actor.wrapper.OnFeatureStepCompletedWrapper;
import info.smart_tools.smartactors.feature_management.interfaces.ifeature.IFeature;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ds_object.DSObject;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.istrategy_container.IStrategyContainer;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.task.interfaces.iqueue.IQueue;
import info.smart_tools.smartactors.task.interfaces.itask.ITask;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Created by sevenbits on 12/21/16.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({ModuleManager.class})
@PowerMockIgnore("jdk.internal.reflect.*")
public class FeatureManagerActorTest extends IOCInitializer {

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    private IQueue queue = mock(IQueue.class);
    private IReceiverChain chain = mock(IReceiverChain.class);
    private IChainStorage storage = mock(IChainStorage.class);
    private IStrategy getChainIDByNameStrategy = mock(IStrategy.class);
    private IStrategy getSequence = mock(IStrategy.class);
    private IStrategy getProcessor = mock(IStrategy.class);

    private IQueue afterFeaturesCallbackQueue1 = mock(IQueue.class);
    private IQueue afterFeaturesCallbackQueue2 = mock(IQueue.class);
    private IQueue afterFeaturesCallbackQueue3 = mock(IQueue.class);

    private IStrategy afterFeaturesCallbackStrategy = mock(IStrategy.class);

    @Before
    public void init()
            throws Exception {
        PowerMockito.spy(ModuleManager.class);
        PowerMockito.doReturn(new HashMap<>()).when(ModuleManager.class, "getModules");

        IOC.register(
                Keys.getKeyByName("task_queue"), new SingletonStrategy(this.queue)
        );
        IOC.register(
                Keys.getKeyByName("chain_id_from_map_name_and_message"), getChainIDByNameStrategy
        );
        IOC.register(
                Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(this.storage)
        );
        IOC.register(
                Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"), this.getSequence
        );
        IOC.register(
                Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"), this.getProcessor
        );
        IOC.register(Keys.getKeyByName(IQueue.class.getCanonicalName()), this.afterFeaturesCallbackStrategy);
    }

    @Test
    public void checkActorCreation()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();
        assertNotNull(actor);
    }

    @Test
    public void checkManagementMethod()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();

        IFeature feature1 = mock(IFeature.class);
        IFeature feature2 = mock(IFeature.class);
        IFeature feature3 = mock(IFeature.class);
        IMessageProcessor mp1 = mock(IMessageProcessor.class);
        IMessageProcessor mp2 = mock(IMessageProcessor.class);
        IMessageProcessor mp3 = mock(IMessageProcessor.class);
        AddFeatureWrapper wrapper = mock(AddFeatureWrapper.class);
        String uuid1 = UUID.randomUUID().toString();
        String uuid2 = UUID.randomUUID().toString();
        String uuid3 = UUID.randomUUID().toString();

        Set<IFeature> features = new HashSet<IFeature>(){{add(feature1); add(feature2);}};
        Set<IFeature> features2 = new HashSet<IFeature>(){{add(feature1); add(feature3);}};

        when(wrapper.getFeatures())
                .thenReturn(features).thenReturn(features)
                .thenReturn(features).thenReturn(features)
                .thenReturn(features2).thenReturn(features2);
        when(wrapper.getMessageProcessor()).thenReturn(mp1).thenReturn(mp2).thenReturn(mp3);
        IObject ctx1 = IOC.resolve(Keys.getKeyByName(IObject.class.getName()));
        IObject ctx2 = IOC.resolve(Keys.getKeyByName(IObject.class.getName()));
        IObject ctx3 = IOC.resolve(Keys.getKeyByName(IObject.class.getName()));
        when(mp1.getContext()).thenReturn(ctx1);
        when(mp2.getContext()).thenReturn(ctx2);
        when(mp3.getContext()).thenReturn(ctx3);
        when(wrapper.getScatterChainName()).thenReturn("chain");
        Object chainId = mock(Object.class);
        when(getChainIDByNameStrategy.resolve("chain")).thenReturn(chainId);
        when(this.storage.resolve(chainId)).thenReturn(this.chain);

        when(feature1.getName()).thenReturn("feature 1");
        when(feature1.getDisplayName()).thenReturn("feature 1");
        when(feature1.getGroupId()).thenReturn("groupId1");
        when(feature1.getDependencies())
                .thenReturn(new HashSet<String>(){{add("groupId2:feature 2");}})
                .thenReturn(new HashSet<>())
                .thenReturn(new HashSet<>())
                .thenReturn(null);
        when(feature1.updateFromClone(any())).thenReturn(true);
        when(feature2.getGroupId()).thenReturn("groupId2");
        when(feature2.getName()).thenReturn("feature 2");
        when(feature2.getDisplayName()).thenReturn("feature 2");
        when(feature2.updateFromClone(any())).thenReturn(true);
        when(feature2.getDependencies())
                .thenReturn(new HashSet<>())
                .thenReturn(new HashSet<>())
                .thenReturn(null);
        when(feature3.getGroupId()).thenReturn("groupId3");
        when(feature3.getName()).thenReturn("feature 3");
        when(feature3.getDisplayName()).thenReturn("feature 3");
        when(feature3.updateFromClone(any())).thenReturn(true);
        when(feature3.getDependencies())
                .thenReturn(new HashSet<String>(){{add("feature 2");}})
                .thenReturn(new HashSet<>())
                .thenReturn(new HashSet<>())
                .thenReturn(null);
        when(feature1.getId()).thenReturn(uuid1);
        when(feature2.getId()).thenReturn(uuid2);
        when(feature3.getId()).thenReturn(uuid3);

        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        when(this.getSequence.resolve(any(), any(), any())).thenReturn(sequence);
        IMessageProcessor mpf1 = mock(IMessageProcessor.class);
        IMessageProcessor mpf2 = mock(IMessageProcessor.class);
        IMessageProcessor mpf3 = mock(IMessageProcessor.class);
        when(this.getProcessor.resolve(this.queue, sequence)).thenReturn(mpf1).thenReturn(mpf2).thenReturn(mpf1).thenReturn(mpf2).thenReturn(mpf1).thenReturn(mpf2).thenReturn(mpf3);

        when(this.afterFeaturesCallbackStrategy.resolve())
                .thenReturn(this.afterFeaturesCallbackQueue1)
                .thenReturn(this.afterFeaturesCallbackQueue2)
                .thenReturn(this.afterFeaturesCallbackQueue3);
        ITask task21 = mock(ITask.class);
        ITask task22 = mock(ITask.class);
        ITask task11 = mock(ITask.class);

        when(this.afterFeaturesCallbackQueue2.tryTake()).thenReturn(task21).thenReturn(task22).thenReturn(null);
        when(this.afterFeaturesCallbackQueue1.tryTake()).thenReturn(task11).thenReturn(null);

        // check addition two new features
        actor.addFeatures(wrapper);

        verify(mp1, times(1)).pauseProcess();
        verify(mpf1, times(1)).process(any(IObject.class), any(IObject.class));
        verify(mpf2, times(1)).process(any(IObject.class), any(IObject.class));

        // check addition two already added features (must be processed anyway)
        actor.addFeatures(wrapper);

        verify(mp2, times(1)).pauseProcess();
        verify(mpf1, times(2)).process(any(IObject.class), any(IObject.class));
        verify(mpf2, times(2)).process(any(IObject.class), any(IObject.class));

        // check addition one new feature and one already added feature
        actor.addFeatures(wrapper);
        verify(mp3, times(1)).pauseProcess();
        verify(mpf1, times(3)).process(any(IObject.class), any(IObject.class));
        verify(mpf2, times(3)).process(any(IObject.class), any(IObject.class));
        //verify(mpf3, times(1)).process(any(IObject.class), any(IObject.class));

        // check 'onFeatureStepCompleted' method
        Map<IMessageProcessor, IFeature> featureProcess = new HashMap<>();
        OnFeatureStepCompletedWrapper onFeatureStepCompletedWrapper = mock(OnFeatureStepCompletedWrapper.class);
        when(onFeatureStepCompletedWrapper.getFeature())
                .thenReturn(feature1)
                .thenReturn(feature2)
                .thenReturn(feature3)
                .thenReturn(feature3)
                .thenReturn(feature2)
                .thenReturn(feature1);
        when(onFeatureStepCompletedWrapper.getMessageProcessor())
                .thenReturn(mpf1)
                .thenReturn(mpf2)
                .thenReturn(mpf3)
                .thenReturn(mpf3)
                .thenReturn(mpf2)
                .thenReturn(mpf1);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);

        verify(mpf1, times(1)).pauseProcess();

        // check 'onFeatureLoaded' method
        when(feature3.isFailed()).thenReturn(true);
        OnFeatureLoadedWrapper onFeatureLoadedWrapper = mock(OnFeatureLoadedWrapper.class);
        when(onFeatureLoadedWrapper.getFeature())
                .thenReturn(feature2)
                .thenReturn(feature1)
                .thenReturn(feature3);
        when(onFeatureLoadedWrapper.getAfterFeaturesCallbackQueue())
                .thenReturn(this.afterFeaturesCallbackQueue2)
                .thenReturn(this.afterFeaturesCallbackQueue1)
                .thenReturn(this.afterFeaturesCallbackQueue3);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);
        actor.onFeatureLoaded(onFeatureLoadedWrapper);
        actor.onFeatureStepCompleted(onFeatureStepCompletedWrapper);
        actor.onFeatureLoaded(onFeatureLoadedWrapper);
        actor.onFeatureLoaded(onFeatureLoadedWrapper);

//        verify(mpf1, times(1)).continueProcess(null);
        verify(mp1).continueProcess(null);
        verify(mp3).continueProcess(null);

        verify(task21, times(1)).execute();
        verify(task22, times(1)).execute();
        verify(task11, times(1)).execute();

        // check 'getState' method
        FeatureManagerStateWrapper stateWrapper = mock(FeatureManagerStateWrapper.class);
        doAnswer(invocationOnMock -> {
            Collection<IFeature> loadedFeatures = (Collection<IFeature>) invocationOnMock.getArguments()[0];
            assertEquals(loadedFeatures.size(), 2);
            assertTrue(loadedFeatures.contains(feature1));
            assertTrue(loadedFeatures.contains(feature2));
            return null;
        }).when(stateWrapper).setLoadedFeatures(any());
        doAnswer(invocationOnMock -> {
            Collection<IFeature> failedFeatures = (Collection<IFeature>) invocationOnMock.getArguments()[0];
            assertEquals(failedFeatures.size(), 1);
            assertTrue(failedFeatures.contains(feature3));
            return null;
        }).when(stateWrapper).setFailedFeatures(any());
        doAnswer(invocationOnMock -> {
            Collection<IFeature> processingFeatures = (Collection<IFeature>) invocationOnMock.getArguments()[0];
            assertEquals(processingFeatures.size(), 0);
            return null;
        }).when(stateWrapper).setProcessingFeatures(any());
        doAnswer(invocationOnMock -> {
            Map frozenRequests = (Map<IMessageProcessor, IFeature>) invocationOnMock.getArguments()[0];
            assertTrue(frozenRequests.isEmpty());
            return null;
        }).when(stateWrapper).setFrozenRequests(any());
        doAnswer(invocationOnMock -> {
            Map frozenFeatures = (Map<IMessageProcessor, Set<IFeature>>) invocationOnMock.getArguments()[0];
            assertTrue(frozenFeatures.isEmpty());
            return null;
        }).when(stateWrapper).setFrozenFeatureProcesses(any());
        actor.getState(stateWrapper);
        verify(stateWrapper, times(1)).setLoadedFeatures(any());
        verify(stateWrapper, times(1)).setFailedFeatures(any());
        verify(stateWrapper, times(1)).setProcessingFeatures(any());
        verify(stateWrapper, times(1)).setFrozenFeatureProcesses(any());
        verify(stateWrapper, times(1)).setFrozenRequests(any());

    }

    @Test (expected = FeatureManagementException.class)
    public void checkFeatureManagementExceptionOnCallAddFeaturesMethod()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();
        AddFeatureWrapper wrapper = mock(AddFeatureWrapper.class);
        when(wrapper.getFeatures()).thenThrow(ReadValueException.class);
        actor.addFeatures(wrapper);
        fail();
    }

    @Test (expected = FeatureManagementException.class)
    public void checkFeatureManagementExceptionOnCallGetStateMethod()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();
        FeatureManagerStateWrapper stateWrapper = mock(FeatureManagerStateWrapper.class);
        doThrow(ChangeValueException.class).when(stateWrapper).setLoadedFeatures(any());
        actor.getState(stateWrapper);
        fail();
    }

    @Test (expected = FeatureManagementException.class)
    public void checkFeatureManagementExceptionOnCallOnFeatureLoadedMethod()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();
        OnFeatureLoadedWrapper wrapper = mock(OnFeatureLoadedWrapper.class);
        when(wrapper.getFeature()).thenThrow(ReadValueException.class);
        actor.onFeatureLoaded(wrapper);
        fail();
    }

    @Test (expected = FeatureManagementException.class)
    public void checkFeatureManagementExceptionOnCallOnFeatureStepCompletedMethod()
            throws Exception {
        FeatureManagerActor actor = new FeatureManagerActor();
        OnFeatureStepCompletedWrapper wrapper = mock(OnFeatureStepCompletedWrapper.class);
        when(wrapper.getFeature()).thenThrow(ReadValueException.class);
        actor.onFeatureStepCompleted(wrapper);
        fail();
    }
}

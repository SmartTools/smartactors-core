package info.smart_tools.smartactors.message_processing.actor_receiver_creator;

import info.smart_tools.smartactors.message_processing.actor_receiver.ActorReceiver;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield.IField;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.message_processing_interfaces.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.base.interfaces.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.message_processing_interfaces.iroutable_object_creator.exceptions.ObjectCreationException;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.scope.iscope.IScope;
import info.smart_tools.smartactors.message_processing_interfaces.iwrapper_generator.IWrapperGenerator;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.ioc.named_keys_storage.Keys;
import info.smart_tools.smartactors.ioc.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.ioc.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.ioc.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;

import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ActorReceiverCreator}
 */
public class ActorReceiverCreatorTest {

    @Before
    public void init()
            throws Exception {
        Object keyOfMainScope = ScopeProvider.createScope(null);
        IScope scope = ScopeProvider.getScope(keyOfMainScope);
        scope.setValue(IOC.getIocKey(), new StrategyContainer());
        ScopeProvider.setCurrentScope(scope);

        IOC.register(
                IOC.getKeyForKeyStorage(),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new Key((String) a[0]);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        })
        );

        Queue queue = mock(Queue.class);
        when(queue.isEmpty()).thenReturn(true);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "actor_receiver_queue"),
                new SingletonStrategy(queue)
        );

        AtomicBoolean isBusy = new AtomicBoolean();
        isBusy.set(false);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "actor_receiver_busyness_flag"),
                new SingletonStrategy(isBusy)
        );

        // register wrapper generator
        IResolveDependencyStrategy wgs = mock(IResolveDependencyStrategy.class);
        IWrapperGenerator wg = mock(IWrapperGenerator.class);
        IOC.register(Keys.getOrAdd(IWrapperGenerator.class.getCanonicalName()), wgs);
        when(wgs.resolve()).thenReturn(wg);
        MethodWrapper mw = new MethodWrapper();
        when(wg.generate(IMethodWrapper.class)).thenReturn(mw);
    }

    @Test
    public void checkCreationAndExecution()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Throwable e) {
                                throw new RuntimeException("Could not create new instance of FieldName", e);
                            }
                        }
                )
        );
        IField field = mock(IField.class);
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), IField.class.getCanonicalName()),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            return field;
                        }
                )
        );
        // register receiver generator
        IResolveDependencyStrategy rgs = mock(IResolveDependencyStrategy.class);
        IReceiverGenerator rg = mock(IReceiverGenerator.class);
        IOC.register(Keys.getOrAdd(IReceiverGenerator.class.getCanonicalName()), rgs);
        when(rgs.resolve()).thenReturn(rg);
        IMessageReceiver mr = mock(IMessageReceiver.class);
        when(rg.generate(any(CustomActor.class), any(IResolveDependencyStrategy.class), any(String.class))).thenReturn(mr);

        IObject objectSection = mock(IObject.class);
        when(objectSection.getValue(new FieldName("name"))).thenReturn("actorID");
        when(objectSection.getValue(new FieldName("dependency"))).thenReturn("createSampleActorStrategy");
        IResolveDependencyStrategy createSampleActorStrategy = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("createSampleActorStrategy"), createSampleActorStrategy);
        ConstructorWrapperImpl wrapperImpl = new ConstructorWrapperImpl();
        CustomActor a = new CustomActor(wrapperImpl);
        when(createSampleActorStrategy.resolve(objectSection))
                .thenReturn(a);

        ActorReceiverCreator arc = new ActorReceiverCreator();
        IRouter router = new Router();

        arc.createObject(router, objectSection);
        assertEquals(((Router) router).map.size(), 1);
        IMessageReceiver actorReceiver = router.route("actorID");
        assertSame(actorReceiver.getClass(), ActorReceiver.class);

        // mock IMessageProcessor, IMessageProcessingSequence, IObject as current sequence
        IMessageProcessor processor = mock(IMessageProcessor.class);
        IMessageProcessingSequence sequence = mock(IMessageProcessingSequence.class);
        IObject currentSequence = mock(IObject.class);
        IObject env = mock(IObject.class);
        when(processor.getEnvironment()).thenReturn(env);
        when(processor.getSequence()).thenReturn(sequence);
        when(sequence.getCurrentReceiverArguments()).thenReturn(currentSequence);
        // configure mock of field
        when(field.in(currentSequence)).thenReturn("getSomeValue");
        // configure handler message receiver mock
        doNothing().when(mr).receive(processor);
        actorReceiver.receive(processor);
        verify(mr, times(1)).receive(processor);
        verify(createSampleActorStrategy, times(1)).resolve(objectSection);
        verify(rg, times(1)).generate(
                any(CustomActor.class),
                any(IResolveDependencyStrategy.class),
                any(String.class)
        );
        verify(objectSection, times(1)).getValue(new FieldName("name"));
        verify(objectSection, times(1)).getValue(new FieldName("dependency"));
    }

    @Test (expected = ObjectCreationException.class)
    public void checkCreationExceptionOnWrongFieldNameStrategy()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return null;
                            } catch (Throwable e) {
                                throw new RuntimeException("Could not create new instance of FieldName", e);
                            }
                        }
                )
        );
        ActorReceiverCreator arc = new ActorReceiverCreator();
        fail();
    }

    @Test (expected = ObjectCreationException.class)
    public void checkMethodExceptionOnWrongArgs()
            throws Exception {
        IOC.register(
                IOC.resolve(IOC.getKeyForKeyStorage(), "info.smart_tools.smartactors.iobject.ifield_name.IFieldName"),
                new ResolveByNameIocStrategy(
                        (a) -> {
                            try {
                                return new FieldName((String) a[0]);
                            } catch (Throwable e) {
                                throw new RuntimeException("Could not create new instance of FieldName", e);
                            }
                        }
                )
        );
        ActorReceiverCreator arc = new ActorReceiverCreator();
        arc.createObject(null, null);
        fail();
    }
}

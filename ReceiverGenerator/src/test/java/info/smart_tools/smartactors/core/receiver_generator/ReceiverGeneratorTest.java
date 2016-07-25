package info.smart_tools.smartactors.core.receiver_generator;

import info.smart_tools.smartactors.core.class_generator_java_compile_api.ClassGenerator;
import info.smart_tools.smartactors.core.ds_object.DSObject;
import info.smart_tools.smartactors.core.field_name.FieldName;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.ioc.IOC;
import info.smart_tools.smartactors.core.ireceiver_generator.IReceiverGenerator;
import info.smart_tools.smartactors.core.ireceiver_generator.exception.ReceiverGeneratorException;
import info.smart_tools.smartactors.core.iresolve_dependency_strategy.IResolveDependencyStrategy;
import info.smart_tools.smartactors.core.iscope.IScope;
import info.smart_tools.smartactors.core.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.core.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.core.named_keys_storage.Keys;
import info.smart_tools.smartactors.core.resolve_by_name_ioc_with_lambda_strategy.ResolveByNameIocStrategy;
import info.smart_tools.smartactors.core.scope_provider.ScopeProvider;
import info.smart_tools.smartactors.core.strategy_container.StrategyContainer;
import info.smart_tools.smartactors.core.string_ioc_key.Key;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ReceiverGenerator}
 */
public class ReceiverGeneratorTest {

    @Before
    public void init() throws Exception {
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
    }

    @Test
    public void checkCreation()
            throws Exception {
        CustomActor a = new CustomActor();
        CustomWrapper w = new CustomWrapper();
        w.setGetterUsed(false);
        w.setSetterUsed(false);
        IResolveDependencyStrategy returnCustomActorStrategy = mock(IResolveDependencyStrategy.class);
        IResolveDependencyStrategy returnWrapperStrategy = mock(IResolveDependencyStrategy.class);
        IOC.register(Keys.getOrAdd("actorID"), returnCustomActorStrategy);
        IOC.register(Keys.getOrAdd(ICustomWrapper.class.getCanonicalName()), returnWrapperStrategy);
        when(returnCustomActorStrategy.resolve()).thenReturn(a);
        when(returnWrapperStrategy.resolve()).thenReturn(w);
        IObject configs = mock(IObject.class);
        IObject env = mock(IObject.class);
        IObject wrapperConfig = mock(IObject.class);
        when(env.getValue(new FieldName("int"))).thenReturn(1);
        doNothing().when(env).setValue(new FieldName("int"), 2);
        IMessageProcessor processor = mock(IMessageProcessor.class);
        when(processor.getEnvironment()).thenReturn(w);
        IReceiverGenerator rg = new ReceiverGenerator(null);
        assertNotNull(rg);
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);
        when(strategy.resolve()).thenReturn(w);
        IMessageReceiver r = rg.generate(a, strategy, "doSomeWork");
        assertNotNull(r);
        r.receive(processor);
        assertTrue(w.getGetterUsed());
        assertTrue(w.getSetterUsed());
    }

    @Test(expected = InvalidArgumentException.class)
    public void checkInvalidArgumentExceptionOnNullParameter()
            throws Exception {
        IReceiverGenerator rg = new ReceiverGenerator(null);
        rg.generate(null, null, null);
        fail();
    }

    @Test(expected = ReceiverGeneratorException.class)
    public void checkReceiverGeneratorExceptionOn()
            throws Exception {
        CustomActor a = new CustomActor();
        IResolveDependencyStrategy strategy = mock(IResolveDependencyStrategy.class);

        IReceiverGenerator rg = new ReceiverGenerator(null);
        rg.generate(a, strategy, "a");
        fail();
    }
}

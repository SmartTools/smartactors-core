package info.smart_tools.smartactors.message_processing.receiver_chain;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.dumpable_interface.idumpable.IDumpable;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.scope.iscope.IScope;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link ImmutableReceiverChain}.
 */
public class ImmutableReceiverChainTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy");
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidNamePassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain(null, mock(IObject.class), new IMessageReceiver[0],
                new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidDescriptionListPassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", null, new IMessageReceiver[0],
                null, mock(Map.class), mock(IScope.class), mock(IModule.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidArgumentsListPassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", mock(IObject.class), new IMessageReceiver[0],
                null, mock(Map.class), mock(IScope.class), mock(IModule.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_numberOfReceiversDoesNotMatchNumberOfArgumentsObjects()
            throws Exception {
        new ImmutableReceiverChain("theChain", mock(IObject.class), new IMessageReceiver[1],
                new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidReceiversListPassed()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain", mock(IObject.class), null,
                new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class)));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidExceptionsMappingGiven()
            throws Exception {
        assertNotNull(new ImmutableReceiverChain("theChain",mock(IObject.class), new IMessageReceiver[0],
                new IObject[0], null, mock(IScope.class), mock(IModule.class)));
    }

    @Test
    public void Should_beConstructedWithValidParameters()
            throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[0];

        IReceiverChain chain = new ImmutableReceiverChain("theChain", mock(IObject.class), receivers, new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class));

        assertEquals("theChain", chain.getId());
    }

    @Test
    public void Should_get_returnMessageReceiversAndArgumentObjects()
            throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[] {
                mock(IMessageReceiver.class),
                mock(IMessageReceiver.class)};
        IObject[] arguments = new IObject[] {
                mock(IObject.class),
                mock(IObject.class)};

        IReceiverChain chain = new ImmutableReceiverChain("theChain", mock(IObject.class), receivers, arguments, mock(Map.class), mock(IScope.class), mock(IModule.class));

        assertSame(receivers[0], chain.get(0));
        assertSame(arguments[0], chain.getArguments(0));
        assertSame(receivers[1], chain.get(1));
        assertSame(arguments[1], chain.getArguments(1));
        assertNull(chain.get(2));
        assertNull(chain.getArguments(2));
    }

    @Test
    public void Should_getExceptionalChainUsingMappingMap()
            throws Exception {
        ModuleManager.setCurrentModule(ModuleManager.getModuleById(ModuleManager.coreId));
        Map<Class<? extends Throwable>, IObject> mappingMap = new HashMap<Class<? extends Throwable>, IObject>() {{
            put(InvalidArgumentException.class, mock(IObject.class));
        }};
        Throwable selfCaused = mock(Throwable.class);

        when(selfCaused.getCause()).thenReturn(selfCaused);

        IReceiverChain chain = new ImmutableReceiverChain("theChain", mock(IObject.class), new IMessageReceiver[0], new IObject[0], mappingMap, mock(IScope.class), mock(IModule.class));

        assertNull(chain.getExceptionalChainNamesAndEnvironments(new NullPointerException()));
        assertNull(chain.getExceptionalChainNamesAndEnvironments(new IllegalStateException()));
        assertNull(chain.getExceptionalChainNamesAndEnvironments(selfCaused));
        assertSame(mappingMap.get(InvalidArgumentException.class), chain.getExceptionalChainNamesAndEnvironments(new InvalidArgumentException("invalid")));
        assertSame(mappingMap.get(InvalidArgumentException.class), chain.getExceptionalChainNamesAndEnvironments(
                new IllegalStateException(new InvalidArgumentException(new Throwable()))));
    }

    @Test
    public void checkGetChainDescriptionMethod() throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[0];
        IObject description = mock(IObject.class);

        IReceiverChain chain = new ImmutableReceiverChain("theChain", description, receivers, new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class));
        assertSame(description, chain.getChainDescription());
    }


    @Test
    public void Should_dumpMethodReturnTheChainDescription() throws Exception {
        IMessageReceiver[] receivers = new IMessageReceiver[0];
        IObject description = mock(IObject.class);

        IReceiverChain chain = new ImmutableReceiverChain("theChain", description, receivers, new IObject[0], mock(Map.class), mock(IScope.class), mock(IModule.class));
        assertSame(description, ((IDumpable) chain).dump(null));
    }

    @Test
    public void Should_provideCollectionOfUniqueExceptionalChains()
            throws Exception {
        IReceiverChain exceptional1 = mock(IReceiverChain.class), exceptional2 = mock(IReceiverChain.class);
        IFieldName chainFN = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
        IObject eobj1 = mock(IObject.class), eobj2 = mock(IObject.class), eobj3 = mock(IObject.class);
        when(eobj1.getValue(eq(chainFN))).thenReturn(exceptional1);
        when(eobj2.getValue(eq(chainFN))).thenReturn(exceptional2);
        when(eobj3.getValue(eq(chainFN))).thenReturn(exceptional1);

        Map<Class<? extends Throwable>, IObject> eMap = new HashMap<>();
        eMap.put(Exception.class, eobj1);
        eMap.put(NullPointerException.class, eobj2);
        eMap.put(RuntimeException.class, eobj3);

        IReceiverChain chain = new ImmutableReceiverChain("theChain", mock(IObject.class), new IMessageReceiver[0], new IObject[0], eMap, mock(IScope.class), mock(IModule.class));

        Collection<Object> eColl = chain.getExceptionalChainNames();

        assertEquals(new HashSet<>(Arrays.asList(exceptional1, exceptional2)), eColl);
    }
}

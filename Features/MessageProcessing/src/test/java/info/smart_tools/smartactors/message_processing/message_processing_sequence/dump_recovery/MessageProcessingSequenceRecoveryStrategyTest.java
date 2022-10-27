package info.smart_tools.smartactors.message_processing.message_processing_sequence.dump_recovery;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.interfaces.imodule.IModule;
import info.smart_tools.smartactors.class_management.interfaces.module_able.IModuleAble;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.scope.scope_able.IScopeAble;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

/**
 * Test for {@link MessageProcessingSequenceRecoveryStrategy}.
 */
public class MessageProcessingSequenceRecoveryStrategyTest extends IOCInitializer {
    private IChainStorage chainStorageMock;
    private IRouter routerMock;
    private IStrategy chainResolutionStrategyMock;

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) String.valueOf(args[0]);
            }
        });

        chainResolutionStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName(IReceiverChain.class.getCanonicalName()), chainResolutionStrategyMock);

        routerMock = mock(IRouter.class);
        chainStorageMock = mock(IChainStorage.class);

        IOC.register(Keys.getKeyByName(IRouter.class.getCanonicalName()), new SingletonStrategy(routerMock));
        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
    }

    @Test
    public void Should_recoverSequenceFromDumpRestoringDumpedChains()
            throws Exception {
        IReceiverChain chainA = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class)),
                       chainB = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));

        IObject seqDump = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                ("{" +
                        "'maxDepth':4," +
                        "'chainsStack':['a','b','a']," +
                        "'stepsStack':[1,4,2]," +
                        "'scopeSwitchingStack':[false,true,false]," +
                        "'chainsDump':{" +
                        "   'a': {'this-is':'chain-a dump'}" +
                        "}" +
                        "}").replace('\'','"'));

        when(chainStorageMock.resolve("b")).thenReturn(chainB);
        when(chainStorageMock.resolve("a")).thenReturn(chainA);//thenThrow(ChainNotFoundException.class);
        when(chainResolutionStrategyMock.resolve(
                eq("a"),
                same(((IObject) seqDump
                        .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainsDump")))
                        .getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "a"))),
                any(),
                same(routerMock)
        )).thenReturn(chainA);
        when(chainA.get(0)).thenReturn(mock(IMessageReceiver.class));
        when(chainA.get(1)).thenReturn(mock(IMessageReceiver.class));
        when(chainA.get(2)).thenReturn(mock(IMessageReceiver.class));
        when(chainA.get(3)).thenReturn(mock(IMessageReceiver.class));
        when(chainB.get(0)).thenReturn(mock(IMessageReceiver.class));
        when(chainB.get(1)).thenReturn(mock(IMessageReceiver.class));
        when(chainB.get(2)).thenReturn(mock(IMessageReceiver.class));
        when(chainB.get(3)).thenReturn(mock(IMessageReceiver.class));
        when(((IScopeAble) chainB).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IScopeAble) chainA).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) chainB).getModule()).thenReturn(mock(IModule.class));
        when(chainA.getName()).thenReturn("a");
        when(chainB.getName()).thenReturn("b");

        IMessageProcessingSequence sequence = new MessageProcessingSequenceRecoveryStrategy().resolve(seqDump, null);

        assertNotNull(sequence);

        assertEquals(2, sequence.getCurrentLevel());
        assertEquals(3, sequence.getStepAtLevel(2));
        assertEquals(4, sequence.getStepAtLevel(1));
        assertEquals(1, sequence.getStepAtLevel(0));

        sequence.setScopeSwitchingChainName("a");
        sequence.callChain("a");
        try {
            sequence.callChain("a");
            fail();
        } catch (NestedChainStackOverflowException ok) {}
    }
}

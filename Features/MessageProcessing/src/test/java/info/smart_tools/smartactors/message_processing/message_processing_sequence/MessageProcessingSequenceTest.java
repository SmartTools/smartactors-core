package info.smart_tools.smartactors.message_processing.message_processing_sequence;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.iaction.IAction;
import info.smart_tools.smartactors.base.interfaces.iaction.exception.ActionExecutionException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.apply_function_to_arguments.ApplyFunctionToArgumentsStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.class_management.interfaces.module_able.IModuleAble;
import info.smart_tools.smartactors.class_management.module_manager.ModuleManager;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.field_name.FieldName;
import info.smart_tools.smartactors.iobject.ifield_name.IFieldName;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing.chain_storage.ChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.ChainChoiceException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NoExceptionHandleChainException;
import info.smart_tools.smartactors.scope.scope_able.IScopeAble;
import info.smart_tools.smartactors.scope.scope_provider.ScopeProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Tests for {@link MessageProcessingSequence}.
 */
public class MessageProcessingSequenceTest extends IOCInitializer {

    @Override
    protected void registry(final String ... strategyNames)
            throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    private IReceiverChain mainChainMock;
    private Object mainChainName;
    private IMessageReceiver[] messageReceiverMocks;
    private IObject[] receiverArgsMocks;
    private IObject contextMock;

    private IFieldName chainFieldName;
    private IFieldName afterActionFieldName;

    private IStrategy makeDumpStrategy;
    private IStrategy chainIdStrategy;
    private IChainStorage chainStorage;
    private IRouter router;

    @Override
    public void registerMocks()
            throws Exception {
        mainChainMock = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        messageReceiverMocks = new IMessageReceiver[10];
        receiverArgsMocks = new IObject[10];

        for (int i = 0; i < messageReceiverMocks.length; i++) {
            messageReceiverMocks[i] = mock(IMessageReceiver.class);
        }

        for (int i = 0; i < receiverArgsMocks.length; i++) {
            receiverArgsMocks[i] = mock(IObject.class);
        }

        contextMock = mock(IObject.class);

        chainFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain");
        afterActionFieldName = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "after");

        makeDumpStrategy = mock(IStrategy.class);
        chainIdStrategy = new ApplyFunctionToArgumentsStrategy(args -> { return args[0]; });
                //mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("make dump"), makeDumpStrategy);
        router = mock(IRouter.class);
        mainChainName = "main chain";
        chainStorage = mock(ChainStorage.class);
        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorage));
        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), chainIdStrategy);
        when(chainStorage.resolve(mainChainName)).thenReturn(mainChainMock);
        when(mainChainMock.getName()).thenReturn(mainChainName);
        when(((IScopeAble) mainChainMock).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) mainChainMock).getModule()).thenReturn(ModuleManager.getCurrentModule());
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_invalidStackDepthGiven()
            throws Exception {
        assertNotNull(new MessageProcessingSequence(0, mainChainName, mock(IObject.class), true));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_nullMainChainGiven()
            throws Exception {
        assertNotNull(new MessageProcessingSequence(1, null, null, true));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrow_When_mainChainContainsNoReceivers()
            throws Exception {
        when(mainChainMock.get(eq(0))).thenReturn(null);
        assertNotNull(new MessageProcessingSequence(1, mainChainName, mock(IObject.class), true));
    }

    @Test(expected = NestedChainStackOverflowException.class)
    public void Should_throw_When_stackOverflowOccurs()
            throws Exception {
        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);

        try {
            messageProcessingSequence.callChain(mainChainName);
            messageProcessingSequence.callChain(mainChainName);
            messageProcessingSequence.callChain(mainChainName);
        } catch (NestedChainStackOverflowException e) {
            fail();
        }

        messageProcessingSequence.callChain(mainChainMock);
    }

    @Test
    public void Should_moveOverAllReceiversInAllNestedChains()
            throws Exception {
        IReceiverChain chainMock1 = mock(IReceiverChain.class);
        IReceiverChain chainMock2 = mock(IReceiverChain.class);
        IReceiverChain chainMock3 = mock(IReceiverChain.class);

        Object chain1 = "chain1";
        Object chain2 = "chain2";
        Object chain3 = "chain3";

        when(chainStorage.resolve(eq(chain1))).thenReturn(chainMock1);
        when(chainStorage.resolve(eq(chain2))).thenReturn(chainMock2);
        when(chainStorage.resolve(eq(chain3))).thenReturn(chainMock3);

        when(chainMock1.getName()).thenReturn(chain1);
        when(chainMock2.getName()).thenReturn(chain2);
        when(chainMock3.getName()).thenReturn(chain3);

        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(mainChainMock.get(eq(1))).thenReturn(messageReceiverMocks[1]);
        when(mainChainMock.get(eq(2))).thenReturn(messageReceiverMocks[2]);

        when(chainMock1.get(eq(0))).thenReturn(messageReceiverMocks[3]);
        when(chainMock1.get(eq(1))).thenReturn(messageReceiverMocks[4]);

        when(chainMock2.get(eq(0))).thenReturn(messageReceiverMocks[5]);
        when(chainMock2.get(eq(1))).thenReturn(messageReceiverMocks[6]);

        when(chainMock3.get(eq(0))).thenReturn(messageReceiverMocks[7]);
        when(chainMock3.get(eq(1))).thenReturn(messageReceiverMocks[8]);

        when(mainChainMock.getArguments(eq(0))).thenReturn(receiverArgsMocks[0]);
        when(mainChainMock.getArguments(eq(1))).thenReturn(receiverArgsMocks[1]);
        when(mainChainMock.getArguments(eq(2))).thenReturn(receiverArgsMocks[2]);

        when(chainMock1.getArguments(eq(0))).thenReturn(receiverArgsMocks[3]);
        when(chainMock1.getArguments(eq(1))).thenReturn(receiverArgsMocks[4]);

        when(chainMock2.getArguments(eq(0))).thenReturn(receiverArgsMocks[5]);
        when(chainMock2.getArguments(eq(1))).thenReturn(receiverArgsMocks[6]);

        when(chainMock3.getArguments(eq(0))).thenReturn(receiverArgsMocks[7]);
        when(chainMock3.getArguments(eq(1))).thenReturn(receiverArgsMocks[8]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);

        assertSame(messageReceiverMocks[0], messageProcessingSequence.getCurrentReceiver());
        assertSame(receiverArgsMocks[0], messageProcessingSequence.getCurrentReceiverArguments());
        assertSame(messageReceiverMocks[0], messageProcessingSequence.getCurrentReceiver());
        assertSame(receiverArgsMocks[0], messageProcessingSequence.getCurrentReceiverArguments());
        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[1], messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.callChain(chain1);

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[3], messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.callChain(chain2);

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[5], messageProcessingSequence.getCurrentReceiver());
        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[6], messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.callChain(chain3);

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[7], messageProcessingSequence.getCurrentReceiver());
        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[8], messageProcessingSequence.getCurrentReceiver());

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[4], messageProcessingSequence.getCurrentReceiver());

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[2], messageProcessingSequence.getCurrentReceiver());

        assertFalse(messageProcessingSequence.next());
        assertNull(messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.reset();
        assertSame(messageReceiverMocks[0], messageProcessingSequence.getCurrentReceiver());
        assertSame(receiverArgsMocks[0], messageProcessingSequence.getCurrentReceiverArguments());
    }

    @Test
    public void Should_catchException_searchForExceptionalChainAndStartItsExecutionIfFound()
            throws Exception {
        IReceiverChain chainMock1 = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IReceiverChain chainMock2 = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IObject exceptionalChainAndEnvMock = mock(IObject.class);
        IReceiverChain exceptionalChainMock = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        Object exceptionalChainName = "exceptional chain";
        IAction afterAction = mock(IAction.class);
        Throwable exception = mock(Throwable.class);

        Object chain1 = "chain1";
        Object chain2 = "chain2";

        when(chainStorage.resolve(eq(chain1))).thenReturn(chainMock1);
        when(chainStorage.resolve(eq(chain2))).thenReturn(chainMock2);
        when(chainStorage.resolve(eq(exceptionalChainName))).thenReturn(exceptionalChainMock);

        when(chainMock1.getName()).thenReturn(chain1);
        when(chainMock2.getName()).thenReturn(chain2);
        when(exceptionalChainMock.getName()).thenReturn(exceptionalChainName);

        when(chainMock2.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(exceptionalChainAndEnvMock);
        when(exceptionalChainAndEnvMock.getValue(eq(this.chainFieldName))).thenReturn(exceptionalChainName);
        when(exceptionalChainAndEnvMock.getValue(eq(this.afterActionFieldName))).thenReturn(afterAction);

        when(exceptionalChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        when(chainMock1.get(eq(0))).thenReturn(messageReceiverMocks[1]);
        when(chainMock2.get(eq(0))).thenReturn(messageReceiverMocks[2]);

        when(mainChainMock.get(0)).thenReturn(messageReceiverMocks[3]);
        when(mainChainMock.get(1)).thenReturn(messageReceiverMocks[4]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);

        assertSame(messageReceiverMocks[3], messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.callChain(chain1);
        messageProcessingSequence.callChain(chain2);

        assertTrue(messageProcessingSequence.next());

        assertSame(messageReceiverMocks[2], messageProcessingSequence.getCurrentReceiver());

        messageProcessingSequence.catchException(exception, contextMock);

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[0], messageProcessingSequence.getCurrentReceiver());

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[1], messageProcessingSequence.getCurrentReceiver());

        assertTrue(messageProcessingSequence.next());
        assertSame(messageReceiverMocks[4], messageProcessingSequence.getCurrentReceiver());
    }

    @Test(expected = NoExceptionHandleChainException.class)
    public void Should_throwWhenNoChainFoundForException()
            throws Exception {
        Throwable exception = mock(Throwable.class);

//        when(mainChainMock.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(mock(IObject.class));
        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        IMessageProcessingSequence sequence = new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true);

        sequence.catchException(exception, contextMock);
    }

    @Test
    public void Should_writeCauseAndCatchPositionsToContext()
            throws Exception {
        Throwable exception = mock(Throwable.class);

        IReceiverChain exceptionalChain = mock(
                IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class)
        );
        IObject exceptionalChainAndEnv = mock(IObject.class);
        IAction afterAction = mock(IAction.class);
        IReceiverChain secondaryChain = mock(IReceiverChain.class);

        Object secondaryChainName = "secondaryChain";
        when(chainStorage.resolve(eq(secondaryChainName))).thenReturn(secondaryChain);
        when(secondaryChain.getName()).thenReturn(secondaryChainName);

        Object exceptionalChainName = "exceptional chain";
        when(chainStorage.resolve(eq(exceptionalChainName))).thenReturn(exceptionalChain);
        when(exceptionalChain.getName()).thenReturn(exceptionalChainName);
        when(((IScopeAble) exceptionalChain).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) exceptionalChain).getModule()).thenReturn(ModuleManager.getCurrentModule());

        when(mainChainMock.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(exceptionalChainAndEnv);
//        when(secondaryChain.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(mock(IObject.class));
        when(exceptionalChainAndEnv.getValue(this.afterActionFieldName)).thenReturn(afterAction);
        when(exceptionalChainAndEnv.getValue(this.chainFieldName)).thenReturn(exceptionalChainName);

        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(secondaryChain.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(secondaryChain.get(eq(1))).thenReturn(messageReceiverMocks[1]);
        when(exceptionalChain.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        MessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true);

        messageProcessingSequence.next();
        messageProcessingSequence.callChain(mainChainName);
        messageProcessingSequence.next();
        messageProcessingSequence.callChain(secondaryChainName);
        messageProcessingSequence.next();
        messageProcessingSequence.callChain(secondaryChainName);
        messageProcessingSequence.next();
        messageProcessingSequence.next();

        messageProcessingSequence.catchException(exception, contextMock);

        verify(contextMock).setValue(same(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "causeLevel")), eq(3));
        verify(contextMock).setValue(same(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "causeStep")), eq(1));
        verify(contextMock).setValue(same(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "catchLevel")), eq(1));
        verify(contextMock).setValue(same(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "catchStep")), eq(0));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_goToThrow_When_positionIsOutOfRange()
            throws Exception {
        new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true).goTo(-1, 0);
    }

    @Test
    public void Should_permitAccessToCurrentStackLevelAndStepIndexesOnAllLevel()
            throws Exception {
        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        MessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true);

        messageProcessingSequence.next();
        messageProcessingSequence.callChain(mainChainName);
        messageProcessingSequence.next();
        messageProcessingSequence.callChain(mainChainName);
        messageProcessingSequence.next();

        assertEquals(2, messageProcessingSequence.getCurrentLevel());
        assertEquals(0, messageProcessingSequence.getStepAtLevel(2));

        messageProcessingSequence.goTo(1,3);

        assertEquals(1, messageProcessingSequence.getCurrentLevel());
        assertEquals(2, messageProcessingSequence.getStepAtLevel(1));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_getStepAtLevelThrow_When_LevelIndexIsNegative()
            throws Exception {
        new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true).getStepAtLevel(-1);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_getStepAtLevelThrow_When_LevelIndexIsGreaterThanIndexOfCurrentLevel()
            throws Exception {
        new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true).getStepAtLevel(1);
    }

    @Test
    public void Should_returnFalse_When_CallEndMethod()
            throws Exception {

        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(mainChainMock.get(eq(1))).thenReturn(messageReceiverMocks[1]);
        when(mainChainMock.get(eq(2))).thenReturn(messageReceiverMocks[2]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);
        messageProcessingSequence.end();
        assertFalse(messageProcessingSequence.next());
    }

    @Test
    public void Should_returnFalse_When_AfterActionCallEndMethod()
            throws Exception {
        Throwable exception = mock(Throwable.class);
        IReceiverChain exceptionalChain = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IObject exceptionalChainAndEnv = mock(IObject.class);
        IAction<IMessageProcessingSequence> afterAction = IMessageProcessingSequence::end;

        Object exceptionalChainName = "exceptional chain";
        when(chainStorage.resolve(eq(exceptionalChainName))).thenReturn(exceptionalChain);
        when(exceptionalChain.getName()).thenReturn(exceptionalChainName);

        when(mainChainMock.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(exceptionalChainAndEnv);
        when(exceptionalChainAndEnv.getValue(this.afterActionFieldName)).thenReturn(afterAction);
        when(exceptionalChainAndEnv.getValue(this.chainFieldName)).thenReturn(exceptionalChainName);

        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(mainChainMock.get(eq(1))).thenReturn(messageReceiverMocks[1]);
        when(mainChainMock.get(eq(2))).thenReturn(messageReceiverMocks[2]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);

        messageProcessingSequence.callChain(mainChainName);

        messageProcessingSequence.catchException(exception, contextMock);

        assertFalse(messageProcessingSequence.next());
    }

    @Test
    public void Should_returnFalse_WhenAfterActionThrowsException() throws Exception {
        Throwable exception = mock(Throwable.class);
        IReceiverChain exceptionalChain = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IObject exceptionalChainAndEnv = mock(IObject.class);
        IAction<IMessageProcessingSequence> afterAction = (mps) -> {throw new ActionExecutionException("exception");};

        Object exceptionalChainName = "exceptional chain";
        when(chainStorage.resolve(eq(exceptionalChainName))).thenReturn(exceptionalChain);
        when(exceptionalChain.getName()).thenReturn(exceptionalChainName);

        when(mainChainMock.getExceptionalChainNamesAndEnvironments(same(exception))).thenReturn(exceptionalChainAndEnv);
        when(exceptionalChainAndEnv.getValue(this.afterActionFieldName)).thenReturn(afterAction);
        when(exceptionalChainAndEnv.getValue(this.chainFieldName)).thenReturn(exceptionalChainName);

        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);
        when(mainChainMock.get(eq(1))).thenReturn(messageReceiverMocks[1]);
        when(mainChainMock.get(eq(2))).thenReturn(messageReceiverMocks[2]);

        IMessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(4, mainChainName, mock(IObject.class), true);
        messageProcessingSequence.catchException(exception, contextMock);

        assertFalse(messageProcessingSequence.next());
    }

    @Test
    public void Should_createDumpOfItsState()
            throws Exception {
        IObject options = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"), "{}");

        IObject dump1 = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'chain':'1'}".replace('\'','"'));
        IObject dump2 = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'chain':'2'}".replace('\'','"'));
        IObject dump3 = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'chain':'3'}".replace('\'','"'));

        Object chain1nm = "chain1";
        Object chain2nm = "chain2";
        Object chain3nm = "chain3";

        IReceiverChain chain1 = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IReceiverChain chain2 = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));
        IReceiverChain chain3 = mock(IReceiverChain.class, withSettings().extraInterfaces(IScopeAble.class, IModuleAble.class));

        when(chainStorage.resolve(eq(chain1nm))).thenReturn(chain1);
        when(chainStorage.resolve(eq(chain2nm))).thenReturn(chain2);
        when(chainStorage.resolve(eq(chain3nm))).thenReturn(chain3);

        when(chain1.getName()).thenReturn(chain1nm);
        when(chain2.getName()).thenReturn(chain2nm);
        when(chain3.getName()).thenReturn(chain3nm);

        when(chain1.getExceptionalChainNames()).thenReturn(Collections.emptyList());
        when(chain2.getExceptionalChainNames()).thenReturn(Collections.singletonList(chain3));
        when(chain3.getExceptionalChainNames()).thenReturn(Collections.emptyList());

        when(makeDumpStrategy.resolve(same(chain1), same(options))).thenReturn(dump1);
        when(makeDumpStrategy.resolve(same(chain2), same(options))).thenReturn(dump2);
        when(makeDumpStrategy.resolve(same(chain3), same(options))).thenReturn(dump3);

        when(chain1.get(0)).thenReturn(messageReceiverMocks[0]);
        when(chain1.get(1)).thenReturn(messageReceiverMocks[1]);

        when(chain2.get(0)).thenReturn(messageReceiverMocks[0]);
        when(chain2.get(1)).thenReturn(messageReceiverMocks[1]);

        when(((IScopeAble) chain1).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) chain1).getModule()).thenReturn(ModuleManager.getCurrentModule());
        when(((IScopeAble) chain2).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) chain2).getModule()).thenReturn(ModuleManager.getCurrentModule());
        when(((IScopeAble) chain3).getScope()).thenReturn(ScopeProvider.getCurrentScope());
        when(((IModuleAble) chain3).getModule()).thenReturn(ModuleManager.getCurrentModule());

        MessageProcessingSequence sequence = new MessageProcessingSequence(10, chain1nm, null, true);

        sequence.next();
        sequence.callChain(chain2nm);
        sequence.next();

        IObject dump = sequence.dump(options);

        assertNotNull(dump);

        /*
        IObject chainsDump = (IObject) dump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainsDump"));

        assertNotNull(chainsDump);

        assertSame(dump1, chainsDump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain1")));
        assertSame(dump2, chainsDump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain2")));
        assertSame(dump3, chainsDump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chain3")));
        */

        Collection ss = (Collection) dump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "stepsStack"));

        assertNotNull(ss);
        assertEquals(Arrays.asList(1,0), ss);

        Collection cs = (Collection) dump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "chainsStack"));

        assertNotNull(cs);
        assertEquals(Arrays.asList("chain1", "chain2"), cs);

        Collection srs = (Collection) dump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "scopeSwitchingStack"));

        assertNotNull(srs);
        assertEquals(Arrays.asList(new Boolean(true), new Boolean(false)), srs);

        assertEquals(10, dump.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "maxDepth")));

        MessageProcessingSequence sequenceFromDump = new MessageProcessingSequence(dump, null);
    }

    @Test
    public void checkMessageReceiveExceptionOnAccessForbidden()
            throws Exception {

        Object chainName = "chain1";
        IReceiverChain chainMock = mock(IReceiverChain.class);
        IObject chainDescriptionMock = mock(IObject.class);
        when(chainMock.getChainDescription()).thenReturn(chainDescriptionMock);
        when(chainDescriptionMock.getValue(new FieldName("externalAccess"))).thenReturn(false);

        when(chainStorage.resolve(eq(chainName))).thenReturn(chainMock);
        when(chainMock.getName()).thenReturn(chainName);
        when(mainChainMock.get(eq(0))).thenReturn(messageReceiverMocks[0]);

        MessageProcessingSequence messageProcessingSequence = new MessageProcessingSequence(5, mainChainName, mock(IObject.class), true);

        IMessageProcessor messageProcessorMock = mock(IMessageProcessor.class);
        IObject contextMock = mock(IObject.class);
        when(contextMock.getValue(new FieldName("fromExternal"))).thenReturn(true);

        when(messageProcessorMock.getContext()).thenReturn(contextMock);
        when(messageProcessorMock.getSequence()).thenReturn(messageProcessingSequence);

        try {
            messageProcessingSequence.callChainSecurely(chainName, messageProcessorMock);
            fail();
        } catch (ChainChoiceException e) {
            verify(contextMock, times(1)).setValue(new FieldName("fromExternal"), false);
            verify(contextMock, times(1)).setValue(new FieldName("accessToChainForbiddenError"), true);
        }
    }
}

package info.smart_tools.smartactors.debugger.sequence_impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.IRouter;
import info.smart_tools.smartactors.message_processing_interfaces.irouter.exceptions.RouteNotFoundException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageReceiver;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.exceptions.NestedChainStackOverflowException;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link DebuggerSequenceImpl}.
 */
@Ignore("Need rewrite tests w/o loading other plugins")
public class DebuggerSequenceImplTest extends IOCInitializer {
    private IRouter routerMock;
    private IMessageProcessingSequence sequenceMock;
    private IMessageReceiver debuggerReceiverMock;
    private IMessageReceiver sequenceReceiverMock = mock(IMessageReceiver.class);
    private IObject sequenceArgumentsMock = mock(IObject.class);
    private Object debuggerAddress = new Object();
    private IStrategy dumpCreationStrategy;

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

//        load(InitializeConfigurationObjectStrategies.class);

    @Override
    protected void registerMocks() throws Exception {
        sequenceMock = mock(IMessageProcessingSequence.class);
        when(sequenceMock.getCurrentReceiver()).thenReturn(sequenceReceiverMock);
        when(sequenceMock.getCurrentReceiverArguments()).thenReturn(sequenceArgumentsMock);

        routerMock = mock(IRouter.class);
        IOC.register(Keys.getKeyByName(IRouter.class.getCanonicalName()), new SingletonStrategy(routerMock));

        debuggerReceiverMock = mock(IMessageReceiver.class);
        when(routerMock.route(same(debuggerAddress))).thenReturn(debuggerReceiverMock);

        dumpCreationStrategy = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("make dump"), dumpCreationStrategy);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenSequenceIsNull()
            throws Exception {
        assertNotNull(new DebuggerSequenceImpl(null, new Object()));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenDebuggerAddressIsNull()
            throws Exception {
        assertNotNull(new DebuggerSequenceImpl(sequenceMock, null));
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_constructorThrowWhenDebuggerAddressIsInvalid()
            throws Exception {
        Object invalidAddress = new Object();
        when(routerMock.route(same(invalidAddress))).thenThrow(RouteNotFoundException.class);
        assertNotNull(new DebuggerSequenceImpl(sequenceMock, invalidAddress));
    }

    @Test
    public void Should_goToDebuggerAtEachStep()
            throws Exception {
        when(sequenceMock.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        IDebuggerSequence sequence = new DebuggerSequenceImpl(sequenceMock, debuggerAddress);

        assertSame(debuggerReceiverMock, sequence.getCurrentReceiver());
        assertNotSame(sequenceArgumentsMock, sequence.getCurrentReceiverArguments());
        assertTrue(sequence.next());

        assertSame(sequenceReceiverMock, sequence.getCurrentReceiver());
        assertSame(sequenceArgumentsMock, sequence.getCurrentReceiverArguments());
        assertTrue(sequence.next());

        assertSame(debuggerReceiverMock, sequence.getCurrentReceiver());
        assertTrue(sequence.next());

        assertSame(sequenceReceiverMock, sequence.getCurrentReceiver());
        assertTrue(sequence.next());

        assertSame(debuggerReceiverMock, sequence.getCurrentReceiver());
        assertTrue(sequence.next());
        assertFalse(sequence.isCompleted());

        assertSame(sequenceReceiverMock, sequence.getCurrentReceiver());
        assertTrue(sequence.next());
        assertTrue(sequence.isCompleted());

        assertSame(debuggerReceiverMock, sequence.getCurrentReceiver());
        assertFalse(sequence.next());
        assertFalse(sequence.next());
    }

    @Test
    public void Should_delegateSequenceOperationsToUnderlyingSequence()
            throws Exception {
        IDebuggerSequence sequence = new DebuggerSequenceImpl(sequenceMock, debuggerAddress);

        sequence.reset();
        verify(sequenceMock).reset();

        sequence.goTo(42,666);
        verify(sequenceMock).goTo(42,666);

        sequence.end();
        verify(sequenceMock).end();

        sequence.getCurrentLevel();
        verify(sequenceMock).getCurrentLevel();

        sequence.getStepAtLevel(10);
        verify(sequenceMock).getStepAtLevel(10);

        sequence.callChain(mock(IReceiverChain.class));
        verify(sequenceMock).callChain(any());
    }

    @Test
    public void Should_handleExceptions()
            throws Exception {
        Exception exception = new Exception();
        IObject exceptionContextMock = mock(IObject.class);

        doThrow(NestedChainStackOverflowException.class).doNothing()
                .when(sequenceMock).catchException(same(exception), same(exceptionContextMock));

        IDebuggerSequence sequence = new DebuggerSequenceImpl(sequenceMock, debuggerAddress);

        assertFalse(sequence.isExceptionOccurred());

        sequence.catchException(exception, exceptionContextMock);

        assertTrue(sequence.isExceptionOccurred());
        assertSame(exception, sequence.getException());

        sequence.processException();

        assertTrue(sequence.isExceptionOccurred());
        assertTrue(sequence.getException() instanceof NestedChainStackOverflowException);

        sequence.processException();

        assertFalse(sequence.isExceptionOccurred());
    }

    @Test
    public void Should_terminateSequenceWhenStopMethodCalled()
            throws Exception {
        when(sequenceMock.next())
                .thenReturn(true)
                .thenReturn(true)
                .thenReturn(false);

        IDebuggerSequence sequence = new DebuggerSequenceImpl(sequenceMock, debuggerAddress);

        assertTrue(sequence.next());
        assertTrue(sequence.next());

        sequence.stop();

        assertFalse(sequence.next());
        assertFalse(sequence.next());
    }

    @Test
    public void Should_createDumpOfUnderlyingSequence()
            throws Exception {
        IObject options = mock(IObject.class);
        IObject dump = mock(IObject.class);
        when(dumpCreationStrategy.resolve(same(sequenceMock), same(options))).thenReturn(dump);

        IObject doneDump = new DebuggerSequenceImpl(sequenceMock, debuggerAddress).dump(options);

        assertSame(dump, doneDump);
    }
}

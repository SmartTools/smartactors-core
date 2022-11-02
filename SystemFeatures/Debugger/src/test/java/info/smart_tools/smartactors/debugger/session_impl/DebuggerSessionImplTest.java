package info.smart_tools.smartactors.debugger.session_impl;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.base.strategy.singleton_strategy.SingletonStrategy;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerBreakpointsStorage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSession;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.iobject.iobject.IObject;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.ichain_storage.IChainStorage;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IReceiverChain;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNotNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link DebuggerSessionImpl}.
 */
public class DebuggerSessionImplTest extends IOCInitializer {
    private IChainStorage chainStorageMock;
    private Object debuggerAddress = new Object();
    private IReceiverChain chainMock = mock(IReceiverChain.class);
    private IObject message;

    private IMessageProcessingSequence innerSequenceMock = mock(IMessageProcessingSequence.class);
    private IDebuggerSequence debuggerSequenceMock;
    private IMessageProcessor messageProcessorMock;
    private IDebuggerBreakpointsStorage breakpointsStorageMock;
    private IStrategy sequenceStrategyMock;
    private IStrategy debuggerSequenceStrategyMock;
    private IStrategy processorStrategyMock;
    private IStrategy sequenceDumpStrategyMock;
    private Object taskQueue = new Object();

    private IDebuggerSession session;

    private Exception exception1 = new Exception();

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        IOC.register(Keys.getKeyByName("chain_id_from_map_name_and_message"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) args[0].toString().concat("__id");
            }
        });

        chainStorageMock = mock(IChainStorage.class);
        IOC.register(Keys.getKeyByName(IChainStorage.class.getCanonicalName()), new SingletonStrategy(chainStorageMock));
        when(chainStorageMock.resolve(eq("the-chain__id"))).thenReturn(chainMock);

        debuggerSequenceMock = mock(IDebuggerSequence.class);
        messageProcessorMock = mock(IMessageProcessor.class);

        breakpointsStorageMock = mock(IDebuggerBreakpointsStorage.class);

        sequenceStrategyMock = mock(IStrategy.class);
        debuggerSequenceStrategyMock = mock(IStrategy.class);
        processorStrategyMock = mock(IStrategy.class);
        sequenceDumpStrategyMock = mock(IStrategy.class);
        message = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'a':'foo','b':'bar'}".replace('\'', '"'));


        when(sequenceStrategyMock.resolve(eq(12), eq("the-chain"), eq(message)))
                .thenReturn(innerSequenceMock)
                .thenThrow(StrategyException.class);
        when(debuggerSequenceStrategyMock.resolve(same(innerSequenceMock), same(debuggerAddress)))
                .thenReturn(debuggerSequenceMock)
                .thenThrow(StrategyException.class);
        when(processorStrategyMock.resolve(same(taskQueue), same(debuggerSequenceMock)))
                .thenReturn(messageProcessorMock)
                .thenThrow(StrategyException.class);
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessingSequence"), sequenceStrategyMock);
        IOC.register(Keys.getKeyByName("new debugger sequence"), debuggerSequenceStrategyMock);
        IOC.register(Keys.getKeyByName("info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor"), processorStrategyMock);
        IOC.register(Keys.getKeyByName("task_queue"), new SingletonStrategy(taskQueue));
        IOC.register(Keys.getKeyByName("make dump"), sequenceDumpStrategyMock);
        IOC.register(Keys.getKeyByName(IDebuggerBreakpointsStorage.class.getCanonicalName()), new SingletonStrategy(breakpointsStorageMock));

        IOC.register(Keys.getKeyByName("value_dependency"), new IStrategy() {
            @Override
            public <T> T resolve(Object... args) throws StrategyException {
                return (T) args[0].toString().concat("_value");
            }
        });
    }

    private Object c(final String name, final Object arg) throws CommandExecutionException, InvalidArgumentException {
        return session.executeCommand(name, arg);
    }

    @Test(expected = InvalidArgumentException.class)
    public void Should_throwWhenUnknownCommandRequired()
            throws Exception {
        session = new DebuggerSessionImpl("the-id", debuggerAddress);

        c("unknown", null);
    }

    @Test
    public void Should_changeAndReturnStepModeLevel()
            throws Exception {
        session = new DebuggerSessionImpl("the-id", debuggerAddress);

        assertEquals("OK", c("stepMode", 3));
        assertEquals(3, c("getStepMode", null));

        assertEquals("OK", c("stepMode", "4"));
        assertEquals(4, c("getStepMode", null));

        assertEquals("OK", c("stepMode", "all"));
        assertEquals(Integer.MAX_VALUE, c("getStepMode", null));

        assertEquals("OK", c("stepMode", "none"));
        assertEquals(-1, c("getStepMode", null));
    }

    @Test
    public void Should_startDebugging()
            throws Exception {
        session = new DebuggerSessionImpl("the-id", debuggerAddress);

        try {
            c("start", null);
            fail("should not start when there is no message set and no chain selected");
        } catch (CommandExecutionException ignore) {}

        c("setMessage", message);
        c("setChain", "the-chain");
        c("setStackDepth", 12.2);

        assertEquals(false, c("isPaused", null));
        assertEquals(false, c("isRunning", null));

        assertEquals("OK", c("start", null));
        verify(messageProcessorMock).process(isNotNull(IObject.class), isNotNull(IObject.class));
        reset(messageProcessorMock);

        assertEquals(false, c("isPaused", null));
        assertEquals(true, c("isRunning", null));
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_notChangeMainChainWhenIsStarted()
            throws Exception {
        Should_startDebugging();
        c("setChain", "the-chain");
    }

    @Test
    public void Should_pauseMessageProcessingWhenStepModeIsEnabled()
            throws Exception {
        Should_startDebugging();

        c("stepMode", "all");

        session.handleInterrupt(messageProcessorMock);
        verify(messageProcessorMock).pauseProcess();

        assertEquals(true, c("isPaused", null));
        assertEquals(false, c("isRunning", null));
    }

    @Test
    public void Should_notPauseMessageProcessingWhenStepModeIsDisabled()
            throws Exception {
        Should_startDebugging();

        c("stepMode", "none");

        session.handleInterrupt(messageProcessorMock);
        verify(messageProcessorMock, never()).pauseProcess();

        assertEquals(false, c("isPaused", null));
        assertEquals(true, c("isRunning", null));
    }

    @Test
    public void Should_pauseProcessingWhenExceptionOccursAndBreakOnExceptionIsTrue()
            throws Exception {
        Should_startDebugging();

        c("stepMode", "none");
        c("setBreakOnException", true);

        when(debuggerSequenceMock.getException()).thenReturn(exception1);
        session.handleInterrupt(messageProcessorMock);
        verify(messageProcessorMock).pauseProcess();
        verify(debuggerSequenceMock, never()).processException();
    }

    @Test
    public void Should_tryToProcessExceptionWhenExceptionOccursAndBreakOnExceptionIsFalse()
            throws Exception {
        Should_startDebugging();

        c("stepMode", "none");
        c("setBreakOnException", false);

        when(debuggerSequenceMock.getException()).thenReturn(exception1);
        when(debuggerSequenceMock.processException()).thenReturn(true);
        session.handleInterrupt(messageProcessorMock);
        verify(debuggerSequenceMock).processException();
    }

    @Test
    public void Should_tryToProcessExceptionWhenExceptionOccursAndBreakOnExceptionIsFalse1()
            throws Exception {
        Should_startDebugging();

        c("stepMode", "none");
        c("setBreakOnException", false);

        when(debuggerSequenceMock.getException()).thenReturn(exception1);
        when(debuggerSequenceMock.processException()).thenReturn(false);
        session.handleInterrupt(messageProcessorMock);
        verify(debuggerSequenceMock).processException();
        verify(messageProcessorMock).pauseProcess();
    }

    @Test(expected = CommandExecutionException.class)
    public void Should_throwWhenProcessExceptionCalledWhenThereIsNoExceptionOccurred()
            throws Exception {
        Should_startDebugging();

        c("processException", null);
    }

    @Test
    public void Should_setSeparateMessageFields()
            throws Exception {
        Should_startDebugging();

        c("stop", null);

        c("setMessageField", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name':'z','value':'x','dependency':'value_dependency'}".replace('\'', '"')));
        c("setMessageField", IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'name':'zz','value':'e'}".replace('\'', '"')));

        IObject msg = (IObject) c("getMessage", null);

        assertEquals("x_value", msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "z")));
        assertEquals("e", msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "zz")));
        assertEquals("foo", msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "a")));
        assertEquals("bar", msg.getValue(IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.ifield_name.IFieldName"), "b")));
    }

    @Test
    public void Should_createStackTrace()
            throws Exception {
        Should_startDebugging();

        Object dump = new Object();
        IObject dumpOptions = mock(IObject.class);

        session.handleInterrupt(messageProcessorMock);

        when(sequenceDumpStrategyMock.resolve(same(debuggerSequenceMock), same(dumpOptions))).thenReturn(dump);

        Object trace = c("getStackTrace", dumpOptions);

        assertSame(dump, trace);
    }

    @Test
    public void Should_pauseMessageProcessingAfterPauseCommand()
            throws Exception {
        Should_startDebugging();

        assertEquals(false, c("isPaused", null));
        assertEquals(true, c("isRunning", null));

        assertEquals(true, c("pause", null));

        assertEquals(false, c("isPaused", null));
        assertEquals(true, c("isRunning", null));

        session.handleInterrupt(messageProcessorMock);
        verify(messageProcessorMock).pauseProcess();
        reset(messageProcessorMock);

        assertEquals(true, c("isPaused", null));
        assertEquals(false, c("isRunning", null));

        assertEquals("OK", c("continue", null));

        assertEquals(false, c("isPaused", null));
        assertEquals(true, c("isRunning", null));
    }

    @Test
    public void Should_stopSequenceWhenClosed()
            throws Exception {
        Should_startDebugging();

        session.close();

        verify(debuggerSequenceMock).stop();
    }

    @Test
    public void Should_listCreatedBreakpoints()
            throws Exception {
        Should_startDebugging();

        c("listBreakpoints", null);

        verify(breakpointsStorageMock).listBreakpoints();
    }

    @Test
    public void Should_createBreakpoints()
            throws Exception {
        Should_startDebugging();

        IObject arg = mock(IObject.class);

        c("setBreakpoint", arg);

        verify(breakpointsStorageMock).addBreakpoint(same(arg));
    }

    @Test
    public void Should_modifyBreakpoints()
            throws Exception {
        Should_startDebugging();

        IObject arg = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'id':'this-is-id'}".replace('\'','"'));

        c("modifyBreakpoint", arg);

        verify(breakpointsStorageMock).modifyBreakpoint(eq("this-is-id"), same(arg));
    }

    @Test
    public void Should_goToGivenPositionOnGoToCommand()
            throws Exception {
        Should_startDebugging();
        c("pause", null);
        session.handleInterrupt(messageProcessorMock);

        IObject arg = IOC.resolve(Keys.getKeyByName("info.smart_tools.smartactors.iobject.iobject.IObject"),
                "{'level':4,'step':42}".replace('\'','"'));

        c("goTo", arg);

        verify(debuggerSequenceMock).goTo(4, 42);
    }

    @Test
    public void Should_callChainOnCallCommand()
            throws Exception {
        Should_startDebugging();
        c("pause", null);
        session.handleInterrupt(messageProcessorMock);

        IReceiverChain chainMock = mock(IReceiverChain.class);
        when(chainStorageMock.resolve(eq("chainName__id"))).thenReturn(chainMock);

        c("call", "chainName");

        verify(debuggerSequenceMock).callChain(eq("chainName"));
    }
}

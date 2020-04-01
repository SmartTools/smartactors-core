package info.smart_tools.smartactors.debugger.actor;

import info.smart_tools.smartactors.base.interfaces.istrategy.IStrategy;
import info.smart_tools.smartactors.base.interfaces.istrategy.exception.StrategyException;
import info.smart_tools.smartactors.debugger.actor.wrappers.CommandMessage;
import info.smart_tools.smartactors.debugger.actor.wrappers.DebuggableMessage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSession;
import info.smart_tools.smartactors.helpers.IOCInitializer.IOCInitializer;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.*;

/**
 * Test for {@link DebuggerActor}.
 */
public class DebuggerActorTest extends IOCInitializer {
    private IStrategy sessionStrategyMock;

    private IDebuggerSession[] session;

    private DebuggableMessage debuggableMessageMock;
    private IMessageProcessor messageProcessorMock = mock(IMessageProcessor.class);
    private CommandMessage commandMessageMock;

    private Object debuggerAddress = new Object();

    @Override
    protected void registry(String... strategyNames) throws Exception {
        registryStrategies("ifieldname strategy", "iobject strategy");
    }

    @Override
    protected void registerMocks() throws Exception {
        sessionStrategyMock = mock(IStrategy.class);
        IOC.register(Keys.getKeyByName("debugger session"), sessionStrategyMock);

        session = new IDebuggerSession[] {
            mock(IDebuggerSession.class), mock(IDebuggerSession.class)
        };
        debuggableMessageMock = mock(DebuggableMessage.class);
        commandMessageMock = mock(CommandMessage.class);
        when(commandMessageMock.getDebuggerAddress()).thenReturn(debuggerAddress);
    }

    private String doCreateSession(final DebuggerActor actor, final IDebuggerSession sessionMock)
            throws Exception {
        reset(sessionStrategyMock, commandMessageMock);
        when(commandMessageMock.getDebuggerAddress()).thenReturn(debuggerAddress);
        when(sessionStrategyMock.resolve(any(), same(debuggerAddress), isNull())).thenReturn(sessionMock);
        when(commandMessageMock.getCommand()).thenReturn("newSession");
        actor.executeCommand(commandMessageMock);

        ArgumentCaptor<Object> sessionIdCaptor = ArgumentCaptor.forClass(Object.class);
        verify(sessionStrategyMock).resolve(sessionIdCaptor.capture());

        return sessionIdCaptor.getAllValues().get(0).toString();
    }

    private List<String> doListSessions(final DebuggerActor actor)
            throws Exception {
        reset(commandMessageMock);
        when(commandMessageMock.getDebuggerAddress()).thenReturn(debuggerAddress);
        when(commandMessageMock.getCommand()).thenReturn("listSessions");
        actor.executeCommand(commandMessageMock);

        ArgumentCaptor<Object> resultCaptor = ArgumentCaptor.forClass(Object.class);
        verify(commandMessageMock).setCommandResult(resultCaptor.capture());

        return (List) resultCaptor.getValue();
    }

    private void doCloseSession(final DebuggerActor actor, final String id)
            throws Exception {
        reset(commandMessageMock);
        when(commandMessageMock.getDebuggerAddress()).thenReturn(debuggerAddress);
        when(commandMessageMock.getCommand()).thenReturn("closeSession");
        when(commandMessageMock.getCommandArguments()).thenReturn(id);

        actor.executeCommand(commandMessageMock);

        verify(commandMessageMock).setCommandResult(eq("OK"));
    }

    @Test
    public void Should_createListAndCloseSessions()
            throws Exception {
        DebuggerActor actor = new DebuggerActor();

        String id1 = doCreateSession(actor, session[0]);
        String id2 = doCreateSession(actor, session[1]);

        List<String> ids = doListSessions(actor);

        assertEquals(new HashSet<>(Arrays.asList(id1, id2)), new HashSet<>(ids));

        doCloseSession(actor, id1);

        ids = doListSessions(actor);

        assertEquals(Collections.singletonList(id2), ids);
        verify(session[0]).close();
    }

    @Test
    public void Should_executeSessionCommands()
            throws Exception {
        Object args = new Object();
        Object result = new Object();

        when(session[0].executeCommand(eq("theCommand"), same(args))).thenReturn(result);

        DebuggerActor actor = new DebuggerActor();

        String id = doCreateSession(actor, session[0]);

        reset(commandMessageMock);
        when(commandMessageMock.getDebuggerAddress()).thenReturn(debuggerAddress);
        when(commandMessageMock.getCommandArguments()).thenReturn(args);
        when(commandMessageMock.getCommand()).thenReturn("theCommand");
        when(commandMessageMock.getSessionId()).thenReturn(id);

        actor.executeCommand(commandMessageMock);

        verify(commandMessageMock).setCommandResult(same(result));
    }

    @Test
    public void Should_processInterrupt()
            throws Exception {
        DebuggerActor actor = new DebuggerActor();

        String id = doCreateSession(actor, session[0]);

        when(debuggableMessageMock.getSessionId()).thenReturn(id);
        when(debuggableMessageMock.getProcessor()).thenReturn(messageProcessorMock);

        actor.interrupt(debuggableMessageMock);

        verify(session[0]).handleInterrupt(same(messageProcessorMock));
    }

    @Test
    public void Should_throwWhenSessionIsNotPresent()
            throws Exception {
        DebuggerActor actor = new DebuggerActor();

        when(commandMessageMock.getCommand()).thenReturn("closeSession");
        when(commandMessageMock.getCommandArguments()).thenReturn("non-exist");

        actor.executeCommand(commandMessageMock);

        verify(commandMessageMock).setException(any());
    }

    @Test
    public void Should_throwWhenCannotResolveSession()
            throws Exception {
        DebuggerActor actor = new DebuggerActor();

        when(sessionStrategyMock.resolve(any(), any(), any())).thenThrow(StrategyException.class);

        when(commandMessageMock.getCommand()).thenReturn("newSession");

        actor.executeCommand(commandMessageMock);

        verify(commandMessageMock).setException(any());
    }
}

package info.smart_tools.smartactors.debugger.actor;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.debugger.actor.wrappers.CommandMessage;
import info.smart_tools.smartactors.debugger.actor.wrappers.DebuggableMessage;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerCommand;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSequence;
import info.smart_tools.smartactors.debugger.interfaces.IDebuggerSession;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.InterruptProcessingException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.SessionNotFoundException;
import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.ioc.exception.ResolutionException;
import info.smart_tools.smartactors.ioc.ioc.IOC;
import info.smart_tools.smartactors.ioc.key_tools.Keys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 *
 */
public class DebuggerActor {
    private final Map<String, IDebuggerSession> sessions = new HashMap<>();
    private final Map<String, IDebuggerCommand> globalCommands = new HashMap<>();

    private Object debuggerAddress;

    {
        globalCommands.put("newSession",
                arg -> {
            String id = UUID.randomUUID().toString();

            try {
                IDebuggerSession session = IOC.resolve(Keys.getKeyByName("debugger session"), id, debuggerAddress, arg);

                sessions.put(id, session);
            } catch (ResolutionException e) {
                throw new CommandExecutionException(e);
            }

            return id;
        });

        globalCommands.put("closeSession",
                arg -> {
            String id = (String) arg;

            try {
                getSession(id).close();
            } catch (SessionNotFoundException e) {
                throw new CommandExecutionException(e);
            } finally {
                sessions.remove(id);
            }

            return "OK";
        });

        globalCommands.put("listSessions",
                arg -> new ArrayList<>(sessions.keySet()));
    }

    /**
     * Execute debugger command.
     *
     * @param message    the message containing command name and arguments
     * @throws ReadValueException if error occurs reading command name, session identifier or command arguments from message
     * @throws ChangeValueException if error occurs saving command result
     * @throws InvalidArgumentException if command name or argument is not valid
     * @throws SessionNotFoundException if cannot find a session to execute command within
     */
    public void executeCommand(final CommandMessage message)
            throws ReadValueException, ChangeValueException, InvalidArgumentException,
                SessionNotFoundException {
        debuggerAddress = message.getDebuggerAddress();
        String command = message.getCommand();

        try {
            if (globalCommands.containsKey(command)) {
                Object args = message.getCommandArguments();

                if (null == args) {
                    args = message.getSessionId();
                }

                message.setCommandResult(globalCommands.get(command).execute(args));
            } else {
                String sessionId = message.getSessionId();
                Object args = message.getCommandArguments();

                message.setCommandResult(getSession(sessionId).executeCommand(command, args));
            }
        } catch (CommandExecutionException e) {
            message.setException(e);
        }
    }

    /**
     * Method the {@link IDebuggerSequence} inserts between any two targets of
     * the sequence.
     *
     * @param message the message being debugged
     * @throws ReadValueException if error occurs reading values from message
     * @throws SessionNotFoundException if cannot find the session the message is associated with
     * @throws InterruptProcessingException if error occurs processing interrupt
     */
    public void interrupt(final DebuggableMessage message)
            throws ReadValueException, SessionNotFoundException, InterruptProcessingException {
        getSession(message.getSessionId()).handleInterrupt(message.getProcessor());
    }

    private IDebuggerSession getSession(final String sessionId) throws SessionNotFoundException {
        if (!sessions.containsKey(sessionId)) {
            throw new SessionNotFoundException(sessionId);
        }

        return sessions.get(sessionId);
    }
}

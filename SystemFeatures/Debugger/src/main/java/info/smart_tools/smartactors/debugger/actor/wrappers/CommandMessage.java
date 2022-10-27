package info.smart_tools.smartactors.debugger.actor.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 * Wrapper interface for debugger command messages.
 */
public interface CommandMessage {
    /**
     * @return name of the command to execute
     * @throws ReadValueException if any error occurs
     */
    String getCommand() throws ReadValueException;

    /**
     * @return identifier of debugger session associated with this message
     * @throws ReadValueException if any error occurs
     */
    String getSessionId() throws ReadValueException;

    /**
     * @return arguments to execute the command with
     * @throws ReadValueException if any error occurs
     */
    Object getCommandArguments() throws ReadValueException;

    /**
     * @param object    result of command execution
     * @throws ChangeValueException if any error occurs
     */
    void setCommandResult(final Object object) throws ChangeValueException;

    /**
     * @return address ("target") of the debugger actor.
     * @throws ReadValueException if any error occurs
     */
    Object getDebuggerAddress() throws ReadValueException;

    /**
     * @param e    the exception occurred executing the command
     * @throws ChangeValueException if any error occurs
     */
    void setException(final Throwable e) throws ChangeValueException;
}

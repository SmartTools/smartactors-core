package info.smart_tools.smartactors.debugger.interfaces;

import info.smart_tools.smartactors.base.exception.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.CommandExecutionException;
import info.smart_tools.smartactors.debugger.interfaces.exceptions.InterruptProcessingException;
import info.smart_tools.smartactors.message_processing_interfaces.message_processing.IMessageProcessor;

/**
 *
 */
public interface IDebuggerSession {
    /**
     * Called by debugger when it receives message associated with this session at {@code interrupt} method.
     *
     * @param messageProcessor    the message processor processing the debuggable message
     * @throws InterruptProcessingException if any error occurs
     */
    void handleInterrupt(IMessageProcessor messageProcessor) throws InterruptProcessingException;

    /**
     * Execute a command in this session.
     *
     * @param name    name of the command to execute
     * @param args    arguments of the command
     * @return result of command execution
     * @throws InvalidArgumentException if command name or arguments are not valid
     * @throws CommandExecutionException if any error occurs executing the command
     */
    Object executeCommand(String name, Object args) throws InvalidArgumentException, CommandExecutionException;

    /**
     * Close this session.
     */
    void close();
}

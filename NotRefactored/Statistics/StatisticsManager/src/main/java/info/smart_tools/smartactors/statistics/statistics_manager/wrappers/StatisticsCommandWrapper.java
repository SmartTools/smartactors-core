package info.smart_tools.smartactors.statistics.statistics_manager.wrappers;

import info.smart_tools.smartactors.iobject.iobject.exception.ChangeValueException;
import info.smart_tools.smartactors.iobject.iobject.exception.ReadValueException;

/**
 *
 */
public interface StatisticsCommandWrapper {
    /**
     * Get command name.
     *
     * @return name of the command, statistics manager should execute
     * @throws ReadValueException if error occurs reading value
     */
    String getCommand() throws ReadValueException;

    /**
     * Get command arguments.
     *
     * @return arguments the command should be executed with
     * @throws ReadValueException if error occurs reading value
     */
    Object getCommandArguments() throws ReadValueException;

    /**
     * Store result of command execution.
     *
     * @param result    the result returned by command
     * @throws ChangeValueException if error occurs recording the value
     */
    void setCommandResult(Object result) throws ChangeValueException;

    /**
     * Store exception occurred executing the command.
     *
     * @param exception    the exception
     * @throws ChangeValueException if error occurs recording the value
     */
    void setException(Throwable exception) throws ChangeValueException;
}

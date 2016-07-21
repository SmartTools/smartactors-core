package info.smart_tools.smartactors.core.db_tasks.commons.executors;

import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;

/**
 * Executor for a some query to database.
 * Contains an universal algorithm of execute a some db query independently of the database type.
 */
public interface IDBQueryExecutor {
    /**
     * Checks a some query on executable.
     * If result is false task must not execute query to database, because query is already done.
     * For example: needs insert document in a some collection,
     *          but query message hasn't contains a field document
     *          then we deem that query done successfully.
     *
     * @param message - query message for checking.
     * @return a result of checking.
     *          If query is executable than <code>true</code>, else <code>false</code>.
     * @exception  InvalidArgumentException when query message has invalid format.
     */
    boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException;

    /**
     * Executes a specific type query to database.
     * Must be prepare a specific task before executes query.
     * @see info.smart_tools.smartactors.core.db_tasks.IDatabaseTask#prepare(IObject)
     *
     * @param query - prepared compiled query for execute.
     * @param message - message with query parameters.
     * @exception TaskExecutionException when errors in during execution query.
     */
    void execute(@Nonnull final ICompiledQuery query, @Nonnull final IObject message) throws TaskExecutionException;
}

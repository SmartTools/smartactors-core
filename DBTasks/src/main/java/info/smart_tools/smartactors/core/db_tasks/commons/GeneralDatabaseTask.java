package info.smart_tools.smartactors.core.db_tasks.commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryBuildException;
import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.commons.executors.IDBQueryExecutor;
import info.smart_tools.smartactors.core.db_tasks.commons.queries.IQueryStatementBuilder;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.sql_commons.QueryStatement;

import javax.annotation.Nonnull;

/**
 * General common thread-unsafe database task for all database oriented tasks.
 * This class realize a "Template Method Pattern".
 * Defines a common group of methods for preparation and execution task;
 *           they must be override in sub tasks.
 */
public abstract class GeneralDatabaseTask implements IDatabaseTask {
    /** Prepared query for execution. */
    private ICompiledQuery query;
    /** Message with parameters for task. */
    private IObject message;
    /** Database connection which used for execution query. */
    private IStorageConnection connection;
    /**
     * Execution flag.
     * If <code>true</code> then task executes query to database else no executes.
     * @see IDBQueryExecutor#isExecutable(IObject)
     */
    private boolean executable;

    /**
     * Default constructor for sub tasks.
     */
    protected GeneralDatabaseTask() { }

    /**
     * Prepares the task for execution.
     * Task prepares a specific query for execution to the postgres database.
     * @see IDatabaseTask#prepare(IObject)
     *
     * Builds and compile query to database by parameters from message.
     * @see GeneralDatabaseTask#query
     * @see GeneralDatabaseTask#message
     *
     * Uses a template methods which must be override in sub task:
     * @see GeneralDatabaseTask#isExecutable(IObject)
     * @see GeneralDatabaseTask#takeCompiledQuery(IStorageConnection, IObject)
     * @see GeneralDatabaseTask#setParameters(ICompiledQuery, IObject)
     * @see GeneralDatabaseTask#setInternalState(ICompiledQuery, IObject)
     *
     * @param queryMessage - query message with parameters for insert query.
     * @see info.smart_tools.smartactors.core.db_tasks.wrappers.insert.IInsertMessage
     *
     * @exception TaskPrepareException  when incoming message has a invalid format
     *              or errors in during preparation the task.
     */
    @Override
    public void prepare(@Nonnull final IObject queryMessage) throws TaskPrepareException {
        try {
            executable = isExecutable(queryMessage);
            if (executable) {
                ICompiledQuery compiledQuery = takeCompiledQuery(connection, queryMessage);
                setInternalState(setParameters(compiledQuery, queryMessage), queryMessage);
            }
        } catch (NullPointerException e) {
            throw new TaskPrepareException("Invalid query message!", e);
        } catch (Throwable e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getCause().getMessage();
            throw new TaskPrepareException("Can't prepare query because: " + errorMessage, e);
        }
    }

    /**
     * Executes the task.
     * Task executes a specific query to the postgres database.
     * @see IDatabaseTask#execute()
     *
     * Before the task execution must be done preparation else throws exception.
     * After the task execution or a throwing exceptions, his internal state resets and needs re-preparing.
     * @see IDatabaseTask#prepare(IObject)
     * @see TaskExecutionException
     *
     * @exception TaskExecutionException when errors in during execution the task.
     */
    @Override
    public void execute() throws TaskExecutionException {
        checkInternalState();
        try {
            if (executable) {
                execute(query, message);
            }
            resetInternalState();
        } catch (Throwable e) {
            resetInternalState();
            String errorMessage = e.getMessage() != null ? e.getMessage() : e.getCause().getMessage();
            throw new TaskExecutionException("Task execution has been failed because: " + errorMessage, e);
        }
    }

    /**
     * @return used database connection.
     */
    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    /**
     * @param storageConnection - used database connection.
     * @see IStorageConnection
     */
    @Override
    public void setConnection(final IStorageConnection storageConnection) {
        connection = storageConnection;
    }

    /**
     * Compiles a some query statement for incoming connection.
     *
     * @param connection - used database connection for compilation query statement.
     * @param queryStatementBuilder - builds query statement.
     * @return compiled query from query statement.
     * @exception  QueryBuildException when errors in during compilation query.
     */
    protected @Nonnull ICompiledQuery compileQuery(@Nonnull final IStorageConnection connection,
                                                   @Nonnull final IQueryStatementBuilder queryStatementBuilder
    ) throws QueryBuildException {
        try {
            QueryStatement queryStatement = queryStatementBuilder.build();
            return connection.compileQuery(queryStatement);
        } catch (NullPointerException e) {
            throw new QueryBuildException("Query compile error because: Invalid incoming parameters!", e);
        } catch (StorageException e) {
            throw new QueryBuildException("Query compile error: " + e.getMessage(), e);
        }
    }

    /**
     * Method which override in sub task, used in preparation task.
     * @see GeneralDatabaseTask#prepare(IObject)
     * Creates a prepared for execution compiled query with parameters.
     * @see ICompiledQuery
     *
     * @param storageConnection - used database connection for compilation query.
     * @param queryMessage - message with query parameters.
     * @return compiled query for incoming connection.
     * @throws QueryBuildException when errors in during obtaining compiled query.
     */
    protected abstract @Nonnull ICompiledQuery takeCompiledQuery(@Nonnull final IStorageConnection storageConnection,
                                                                 @Nonnull final IObject queryMessage
    ) throws QueryBuildException;

    /**
     * Sets special for query parameters values from message in compiled query.
     *
     * @param compiledQuery - query without parameters values.
     * @param queryMessage - message with parameters for query.
     * @return a parametrized compiled query.
     * @throws QueryBuildException when errors in during parameterization query.
     */
    protected abstract @Nonnull ICompiledQuery setParameters(@Nonnull final ICompiledQuery compiledQuery,
                                                             @Nonnull final IObject queryMessage
    ) throws QueryBuildException;

    /**
     * Executes the query to database.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * @param compiledQuery - prepared compiled query for execution.
     * @param queryMessage - query message with parameters for query.
     * @exception TaskExecutionException when number of deleted rows not equals
     *              of number of rows which had to be removed,
     *              or errors in during execution create collection query to database.
     */
    protected abstract void execute(@Nonnull final ICompiledQuery compiledQuery,
                                    @Nonnull final IObject queryMessage
    ) throws TaskExecutionException;

    /**
     * Checks a some query on executable.
     * If result is false task must not execute query to database, because query is already done.
     * For example: needs insert document in a some collection,
     *          but query message hasn't contains a field document
     *          then we deem that query done successfully.
     *
     * @param queryMessage - query message for checking.
     * @return a result of checking.
     *          If query is executable than <code>true</code>, else <code>false</code>.
     * @exception  InvalidArgumentException when query message has invalid format.
     */
    protected abstract boolean isExecutable(@Nonnull final IObject queryMessage)
            throws InvalidArgumentException;


    /* Internal method. */
    private void setInternalState(final ICompiledQuery compiledQuery, final IObject queryMessage) {
        this.query = compiledQuery;
        this.message = queryMessage;
    }

    private void resetInternalState() {
        executable = false;
        setInternalState(null, null);
    }

    private void checkInternalState() throws TaskExecutionException {
        if (query == null || message == null) {
            throw new TaskExecutionException("Prepare task before execution!");
        }
    }
}

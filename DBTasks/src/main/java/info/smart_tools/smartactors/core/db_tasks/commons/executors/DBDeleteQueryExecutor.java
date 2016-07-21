package info.smart_tools.smartactors.core.db_tasks.commons.executors;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.db_tasks.commons.DBQueryFields;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.iobject.exception.ReadValueException;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;

/**
 * Common executor for delete query to database.
 * Not dependent of database type.
 * All delete tasks may use this executor.
 * @see IDBQueryExecutor
 */
public final class DBDeleteQueryExecutor implements IDBQueryExecutor {

    private DBDeleteQueryExecutor() {}

    /**
     * Factory-method for creation a new instance of {@link DBDeleteQueryExecutor}.
     * @return a new instance of {@link DBDeleteQueryExecutor}.
     */
    public static DBDeleteQueryExecutor create() {
        return new DBDeleteQueryExecutor();
    }

    /**
     * Checks the create collection query on executable.
     * @see IDBQueryExecutor#isExecutable(IObject)
     *
     * @param message - query message with a some parameters for query.
     * @return <code>true</code> if message contains a document id, else false.
     * @exception InvalidArgumentException when incoming message has invalid format.
     */
    @Override
    public boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        try {
            return DBQueryFields.DOCUMENT_ID.in(message) != null;
        } catch (ReadValueException e) {
            throw new InvalidArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Executes the delete query.
     * One call = one delete.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * @param query - prepared compiled query for execution.
     * @param message - query message with parameters for query.
     * @exception TaskExecutionException when number of deleted rows not equals
     *              of number of rows which had to be removed,
     *              or errors in during execution create collection query to database.
     */
    @Override
    public void execute(@Nonnull final ICompiledQuery query, @Nonnull final IObject message)
            throws TaskExecutionException {
        try {
            int nDeleted = query.executeUpdate();
            if (nDeleted != 1) {
                throw new TaskExecutionException("'Delete query' execution has been failed: " +
                        "wrong count of documents is deleted.");
            }
        } catch (QueryExecutionException e) {
            throw new TaskExecutionException("'Delete task' execution has been failed because:" + e.getMessage(), e);
        }
    }
}

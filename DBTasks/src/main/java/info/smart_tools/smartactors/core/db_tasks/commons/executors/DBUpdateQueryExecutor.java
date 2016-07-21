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
 * Common executor for update query to database.
 * Not dependent of database type.
 * All update tasks may use this executor.
 * @see IDBQueryExecutor
 */
public class DBUpdateQueryExecutor implements IDBQueryExecutor {

    private DBUpdateQueryExecutor() { }

    /**
     * Factory-method for creation a new instance of {@link DBUpdateQueryExecutor}.
     * @return a new instance of {@link DBUpdateQueryExecutor}.
     */
    public static DBUpdateQueryExecutor create() {
        return new DBUpdateQueryExecutor();
    }

    /**
     * Checks the update query on executable.
     * @see IDBQueryExecutor#isExecutable(IObject)
     *
     * @param message - query message with a some parameters for query.
     * @return <code>true</code> if incoming message contains a document, else <code>false</code>.
     * @exception InvalidArgumentException when the incoming message has invalid format.
     */
    @Override
    public boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        try {
            return DBQueryFields.DOCUMENT.in(message) != null;
        } catch (ReadValueException e) {
            throw new InvalidArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Executes the update query.
     * One call = one update.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * @param query - prepared compiled query for execution.
     * @param message - query message with parameters for query.
     * @exception TaskExecutionException when the result set size
     *              hasn't equals of number of updating documents or
     *              errors in during execution create collection query to database.
     */
    @Override
    public void execute(@Nonnull final ICompiledQuery query, @Nonnull final IObject message)
            throws TaskExecutionException {
        try {
            int nUpdated = query.executeUpdate();
            if (nUpdated != 1) {
                throw new QueryExecutionException("'Update query' execution failed: wrong count of documents is updated.");
            }
        } catch (QueryExecutionException e) {
            throw new TaskExecutionException("Update query execution failed because:" + e.getMessage(), e);
        }
    }
}

package info.smart_tools.smartactors.core.db_tasks.commons.executors;

import info.smart_tools.smartactors.core.db_storage.exceptions.QueryExecutionException;
import info.smart_tools.smartactors.core.db_storage.interfaces.ICompiledQuery;
import info.smart_tools.smartactors.core.invalid_argument_exception.InvalidArgumentException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

import javax.annotation.Nonnull;

/**
 * Common executor for create collection query to database.
 * Not dependent of database type.
 * All create collection tasks may use this executor.
 * @see IDBQueryExecutor
 */
public final class DBCreateCollectionQueryExecutor implements IDBQueryExecutor {

    private DBCreateCollectionQueryExecutor() { }

    /**
     * Factory-method for creation a new instance of {@link DBCreateCollectionQueryExecutor}.
     * @return a new instance of {@link DBCreateCollectionQueryExecutor}.
     */
    public static DBCreateCollectionQueryExecutor create() {
        return new DBCreateCollectionQueryExecutor();
    }

    /**
     * Checks the create collection query on executable.
     * @see IDBQueryExecutor#isExecutable(IObject)
     * Always gives <code>true</code> because create collection query hasn't optionals parameters.
     *
     * @param message - query message with a some parameters for query.
     * @return always <code>true</code>.
     * @exception InvalidArgumentException never throws in current executor.
     */
    @Override
    public boolean isExecutable(@Nonnull final IObject message) throws InvalidArgumentException {
        return true;
    }

    /**
     * Executes the create collection query.
     * One call = one creation.
     * @see IDBQueryExecutor#execute(ICompiledQuery, IObject)
     *
     * @param query - prepared compiled query for execution.
     * @param message - query message with parameters for query.
     * @exception TaskExecutionException when errors in during execution create collection query to database.
     */
    @Override
    public void execute(@Nonnull final ICompiledQuery query,
                        @Nonnull final IObject message
    ) throws TaskExecutionException {
        try {
            query.execute();
        } catch (QueryExecutionException e) {
            throw new TaskExecutionException("'Create collection task' execution has been failed because: ", e);
        }
    }
}

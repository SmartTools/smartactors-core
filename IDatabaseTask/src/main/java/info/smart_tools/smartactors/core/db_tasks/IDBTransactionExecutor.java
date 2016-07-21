package info.smart_tools.smartactors.core.db_tasks;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.exception.TransactionExecutionException;

/**
 * Execute tasks in database in transaction.
 * @see IDatabaseTask
 *
 * NOTE: The execution of a transaction can slow down the execution of the query,
 *              so do not use singleton pattern in implementations.
 */
public interface IDBTransactionExecutor {
    /**
     * Creates transaction in database and executes tasks.
     *              If the tasks executed successfully then changes are committed in database,
     *              else changes rollback.
     *
     * @param tasks - an executable tasks to database.
     * @see IDatabaseTask
     * @param connection - a connection to database.
     * @see IStorageConnection
     *
     * @exception TransactionExecutionException
     *          when an error occurred task executing or an error occurred commit.
     */
    void executeTransaction(final IStorageConnection connection, final IDatabaseTask... tasks)
            throws TransactionExecutionException;
}

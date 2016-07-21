package info.smart_tools.smartactors.core.db_tasks.utils;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDBTransactionExecutor;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TransactionExecutionException;

/**
 * Execute task in database in transaction.
 */
public class DBTaskTransactionExecutor implements IDBTransactionExecutor {

    /**
     * Create new instance of {@link DBTaskTransactionExecutor}.
     */
    private DBTaskTransactionExecutor() {}

    /**
     * Factory method for creation new instance of {@link DBTaskTransactionExecutor}.
     *
     * @return new instance of {@link DBTaskTransactionExecutor}.
     */
    public static DBTaskTransactionExecutor create() {
        return new DBTaskTransactionExecutor();
    }

    /**
     * Creates transaction in database and executes task.
     *              If the task executed successfully then changes are committed in database,
     *              else changes rollback.
     *
     * @param tasks - an executable tasks to database.
     * @param connection - a connection to database.
     *
     * @exception TransactionExecutionException when an error occurred task executing
     *              or an error occurred commit.
     */
    @Override
    public void executeTransaction(final IStorageConnection connection,
                                   final IDatabaseTask... tasks
    ) throws TransactionExecutionException {
        try {
            for (IDatabaseTask task : tasks) {
                task.execute();
            }
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (StorageException ee) {
                throw new TransactionExecutionException(connection, e);
            }

            throw new TransactionExecutionException(tasks, connection, e);
        }
    }
}

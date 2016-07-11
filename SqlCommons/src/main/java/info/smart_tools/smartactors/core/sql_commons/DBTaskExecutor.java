package info.smart_tools.smartactors.core.sql_commons;

import info.smart_tools.smartactors.core.db_storage.exceptions.StorageException;
import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;

/**
 * Execute task in database.
 */
public class DBTaskExecutor {

    /**
     * Create new instance of {@link DBTaskExecutor}.
     */
    private DBTaskExecutor() {}

    /**
     * Factory method for creation new instance of {@link DBTaskExecutor}.
     *
     * @return new instance of {@link DBTaskExecutor}.
     */
    public static DBTaskExecutor create() {
        return new DBTaskExecutor();
    }

    /**
     * Creates transaction in database and executes task.
     *              If the task executed successfully then changes are committed in database,
     *              else changes rollback.
     *
     * @param tasks - an executable tasks to database.
     * @param connection - a connection to database.
     *
     * @throws Exception when an error occurred task executing or an error occurred commit.
     */
    public static void executeTransaction(final IStorageConnection connection,
                                          final IDatabaseTask... tasks
    ) throws Exception {
        try {
            for (IDatabaseTask task : tasks) {
                task.execute();
            }
            connection.commit();
        } catch(Exception e) {
            try {
                connection.rollback();
            } catch (StorageException ee) {
                e.addSuppressed(ee);
            }

            throw e;
        }
    }
}

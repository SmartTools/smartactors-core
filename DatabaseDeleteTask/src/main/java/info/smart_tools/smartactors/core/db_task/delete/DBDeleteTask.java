package info.smart_tools.smartactors.core.db_task.delete;

import info.smart_tools.smartactors.core.db_storage.interfaces.CompiledQuery;
import info.smart_tools.smartactors.core.db_task.delete.wrappers.DeletionQuery;
import info.smart_tools.smartactors.core.idatabase_task.IDatabaseTask;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;
import info.smart_tools.smartactors.core.sql_commons.JDBCCompiledQuery;

import javax.annotation.Nonnull;
import java.sql.SQLException;

/**
 * Common deletion task executor.
 */
public abstract class DBDeleteTask implements IDatabaseTask {

    /**
     * Default constructor.
     */
    protected DBDeleteTask() {}

    /**
     * Executes deletion query of rows to database.
     *
     * @param query - a compiled executable query to database.
     * @param message - source message with parameters for query.
     *
     * @throws TaskExecutionException when number of deleted rows not equals of number of rows which had to be removed.
     */
    protected void execute(final @Nonnull CompiledQuery query, final @Nonnull DeletionQuery message)
            throws TaskExecutionException {
        try {
            int nDeleted = ((JDBCCompiledQuery) query).getPreparedStatement().executeUpdate();

            if (nDeleted != message.countDocumentIds()) {
                throw new TaskExecutionException("Delete query failed: wrong count of documents is deleted.");
            }
        } catch (SQLException e) {
            throw new TaskExecutionException("Deletion query execution failed because of SQL exception.", e);
        }
    }
}

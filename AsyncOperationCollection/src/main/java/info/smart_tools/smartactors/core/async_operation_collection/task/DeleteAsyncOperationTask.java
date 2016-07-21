package info.smart_tools.smartactors.core.async_operation_collection.task;

import info.smart_tools.smartactors.core.db_storage.interfaces.IStorageConnection;
import info.smart_tools.smartactors.core.db_tasks.IDatabaseTask;
import info.smart_tools.smartactors.core.db_tasks.exception.TaskPrepareException;
import info.smart_tools.smartactors.core.iobject.IObject;
import info.smart_tools.smartactors.core.itask.exception.TaskExecutionException;

/**
 * Task-facade for delete task for async operations collection
 */
public class DeleteAsyncOperationTask implements IDatabaseTask {
    private IStorageConnection connection;
    private IDatabaseTask deleteTask;

    /**
     * Constructor
     * @param deleteTask nested task for delete object
     */
    public DeleteAsyncOperationTask(final IDatabaseTask deleteTask) {
        this.deleteTask = deleteTask;
    }

    @Override
    public void prepare(final IObject query) throws TaskPrepareException {
        deleteTask.prepare(query);
    }

    @Override
    public void setConnection(final IStorageConnection connection) {
        this.connection = connection;
        deleteTask.setConnection(connection);
    }

    @Override
    public IStorageConnection getConnection() {
        return connection;
    }

    @Override
    public void execute() throws TaskExecutionException {
        deleteTask.execute();
    }
}
